/*
 * Copyright (C) 2017 Alejandro Hernández Ferrero
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package game;

import game.EnemyType.Enemy;
import game.FoodType.Food;
import game.UnitType.Unit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import libqew.ExtensiblePanel;
import libqew.Frame;
import resources.Resources;
import ui.Menu;
import utils.ColorUtils;
import utils.FPSCounter;
import static utils.GeometryUtils.intersect;
import static utils.GeometryUtils.reflectAngleOnXAxis;
import static utils.GeometryUtils.reflectAngleOnYAxis;

/**
 * Clase principal que se encarga de gestionar el juego.
 *
 * @author Alejandro Hernández Ferrero
 */
public class Game extends Frame implements Runnable {

    public static void main(String[] args) {
        try {
            // Busca el look and feel
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }
        new Game();
    }

    private Thread thread;
    private final Physics physics;
    private final Graphics graphics;
    private final Menu menu;
    private final ArrayList<ElementType> elements;
    private boolean stop;
    private long prevTime;
    private float deltaTime;
    private float acc;
    private boolean paused;
    private boolean running;
    private final Snake snake;
    private final FPSCounter fps;
    private String gameoverMessage;

    private Dimension resolution;
    private Color background;
    private float ms = 1000f / 60f;
    private boolean showFPS = false;

    /**
     * Crea un nuevo juego.
     */
    public Game() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Snake");
        setIconImages(Resources.getIcons());
        setResizable(false);
        setLocationByPlatform(true);
        elements = new ArrayList<>();
        graphics = new Graphics();
        physics = new Physics();
        new KeyBindings();
        setResolution(new Dimension(640, 550));
        setGameBackground(new ColorUtils.ColorName(214, 217, 223, 255));
        fps = new FPSCounter();
        snake = new Snake(physics, graphics);
        elements.add(snake);
        FoodType food = new FoodType(physics);
        elements.add(food);
        Enemies enemies = new Enemies(physics, snake);
        elements.add(enemies);
        menu = new Menu(this, snake, enemies, food);
        setJMenuBar(menu);
        setVisible(true);
        graphics.getActionMap().put("PAUSE", new AbstractAction() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (isRunning()) {
                                        if (paused) { // Con el mismo botón se pausa y reanuda
                                            resume();
                                        } else {
                                            pause();
                                        }
                                    }
                                }
                            });
        add(graphics);
        pack();
        reset();
    }

    /**
     * Comprueba si el juego está activo.
     *
     * @return <code>true</code> si el juego está activo
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Empieza un nuevo juego.
     */
    public void start() {
        if (!isRunning()) {
            stop = false;
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Detiene el juego.
     */
    public void stop() {
        stop = true;
    }

    /**
     * Pausa el juego.
     */
    public void pause() {
        if (isRunning()) {
            paused = true;
        }
    }

    /**
     * Reanuda el juego.
     */
    public synchronized void resume() {
        if (paused) {
            paused = false;
            notify();
        }
    }

    /**
     * Reinicia el juego.
     */
    public void reset() {
        physics.init();
        graphics.init();
        synchronized (elements) {
            for (ElementType element : elements) {
                element.init();
            }
        }
        repaint();
    }

    private void gameOver() {
        // Muestra mensaje y reinicia
        gameoverMessage = "<html><font color=#b70000>" + gameoverMessage + "</font>";
        String score = "<br><br><b>Score: " + (snake.getShapes().size() - 1) + "</b></html>";
        JOptionPane.showMessageDialog(this, gameoverMessage + score, "Game Over", JOptionPane.ERROR_MESSAGE);
        reset();
    }

    /**
     * Bucle principal que se encarga de actualizar el juego continuamente.
     */
    @Override
    public void run() {
        running = true;
        prevTime = System.nanoTime();
        acc = -getMS(); // Para que sea 0 en la primera iteración
        while (!stop) {
            try {
                long time = System.nanoTime();
                deltaTime = (time - prevTime) / 1000000000f; // Tiempo desde el anterior ciclo en segundos
                prevTime = time;
                update(deltaTime);
                fps.update(deltaTime);
                repaint();
                acc += getMS() - deltaTime * 1000; // Diferencia entre el tiempo que debió transcurrir y el que transcurrió, en milisegundos
                // No se acumula más de 1 ciclo de desfase ya que conllevaría arrastrarlo y tener más o menos ciclos de la cuenta en el futuro
                if (acc <= -getMS()) { // Si ya se retrasó en un ciclo no es necesario dormir y se va al siguiente
                    acc = -getMS();
                    continue;
                } else if (acc > getMS()) { // No se intenta retrasar más
                    acc = getMS();
                }
                if (paused) { // Se pausa entre ciclos para dejar un estado consistente
                    synchronized (this) {
                        while (paused) { // Evita despertarse involuntariamente
                            wait();
                        }
                        prevTime = System.nanoTime(); // Al despertarse actualiza el tiempo
                    }
                } else {
                    // Hay que tener en cuenta el tiempo que se consumió en el propio bucle 
                    // y el tiempo que durmió de más o de menos debido a imprecisiones de la máquina
                    // para descontarlo y ajustarse más exactamente a los fps deseados
                    sleep(Math.round(getMS() + acc)); // Duerme el tiempo necesario
                }
            } catch (InterruptedException ex) {
            }
        }
        gameOver(); // Cuando se para es porque se pierde
        running = false;
    }

    /**
     * Actualiza todo el juego.
     */
    private void update(float deltaTime) {
        synchronized (elements) {
            for (ElementType element : elements) {
                element.update(deltaTime);
            }
        }
    }

    /**
     * Obtiene las leyes físicas del juego.
     *
     * @return las leyes físicas del juego
     */
    public Physics physics() {
        return physics;
    }

    /**
     * Obtiene los gráficos de este juego.
     *
     * @return los gráficos
     */
    public Graphics graphics() {
        return graphics;
    }

    /**
     * Obtiene la resolución del juego.
     *
     * @return la resolución del juego
     */
    public Dimension getResolution() {
        return resolution;
    }

    /**
     * Establece la resolución del juego.
     *
     * @param resolution la resolución del juego
     */
    public void setResolution(Dimension resolution) {
        this.resolution = resolution;
    }

    /**
     * Obtiene el tiempo en milisegundos que se desea que transcurra entre
     * actualizaciones de la pantalla.
     * <p>
     * Equivale a <code>1000/fps</code>.
     *
     * @return el tiempo en milisegundos que se desea que transcurra entre
     * actualizaciones de la pantalla
     */
    public float getMS() {
        return ms;
    }

    /**
     * Establece el tiempo en milisegundos que se desea que transcurra entre
     * actualizaciones de la pantalla.
     * <p>
     * Equivale a <code>1000/fps</code>.
     *
     * @param ms el tiempo en milisegundos que se desea que transcurra entre
     * actualizaciones de la pantalla
     */
    public void setMS(float ms) {
        this.ms = ms;
    }

    /**
     * Obtiene el color de fondo.
     *
     * @return el color de fondo
     */
    public Color getGameBackground() {
        return background;
    }

    /**
     * Establece el color de fondo.
     *
     * @param background el color de fondo
     */
    public void setGameBackground(Color background) {
        this.background = background;
        graphics.setBackground(background);
    }

    /**
     * Comprueba si se muestran los fps.
     *
     * @return <code>true</code> si se muestran los fps
     */
    public boolean isShowFPS() {
        return showFPS;
    }

    /**
     * Establece si deben mostrarse los fps.
     *
     * @param showFPS  <code>true</code> si deben mostrarse los fps
     */
    public void setShowFPS(boolean showFPS) {
        this.showFPS = showFPS;
    }

    /**
     * Leyes físicas del juego. Es el responsable de comprobar las colisiones
     * entre cuerpos y resolver las consecuencias.
     */
    public class Physics {

        private int generated;

        /**
         * Se inicializa.
         */
        public void init() {
            generated = 1;
        }

        /**
         * Busca una posición libre para un cuerpo con ese radio. Por cuestiones
         * de rendimiento no garantiza que se encuentre si existe alguna en un
         * espacio demasiado poblado.
         *
         * @param radius el radio del cuerpo
         * @return una posición libre, <code>null</code> si no se encuentra
         * ninguno
         */
        public Point generateRandomPosition(int radius) {
            Rectangle rectangle = new Rectangle(radius * 2, radius * 2); // Área que debe haber libre
            Point p = new Point();
            Random r = new Random();
            boolean found = false;
            // El número de intentos será adaptativo y dependerá de la probabilidad estimada de encontrarse
            // Si la probabilidad es alta se darán muchos, ya que será cuestión de tiempo encontrarlo y sería negligente no hacerlo si hay mala suerte
            // Sin embargo, si hay poca esperanza no se invierte mucho tiempo ya que lo más probable es que no exista
            // Si el juego está bien configurado nunca debería llegarse a esta situación, 
            // pero de esta forma es robusto para casos en los que se creen más enemigos de los que entran en la pantalla, con lo que se evita un bucle infinito
            // y se proporciona una solución de compromiso que intente rellenarla al máximo sin demasiado esfuerzo y se desechen los casos difíciles
            int trys = (getBounds().width * getBounds().height) / (radius * 2 * radius * 2);
            trys = (int) Math.ceil(trys * 0.2f + trys / generated * 0.8f);
loop:       do {
                trys--;
                if (trys < 0) { // Si no hay más intentos deja de buscar
                    return null;
                }
                // Genera una posición aleatoria
                p.x = r.nextInt(graphics.getWidth());
                p.y = r.nextInt(graphics.getHeight());
                rectangle.setLocation(p.x - radius, p.y - radius); // Simula el espacio necesario
                if (!graphics.getBounds().contains(rectangle)) { // Si se sale de la pantalla no es válido
                    continue;
                }
                synchronized (elements) {
                    for (ElementType element : elements) {
                        for (ShapeType.Shape shape : element.getShapes()) {
                            if (shape.getShape().intersects(rectangle)) { // Comprueba si entra en conflicto con otro cuerpo
                                continue loop;
                            }
                        }
                    }
                }
                found = true; // Si no hubo conflictos es que es válido
            } while (!found);
            generated++;
            return p;
        }

        /**
         * Comprueba si la posición actual del cuerpo es físicamente válida y
         * genera sus consecuencias.
         *
         * @param a el cuerpo
         * @return <code>true</code> si la posición es válida
         */
        public boolean validatePosition(Unit a) {
            if (stop) {
                return false;
            }
            boolean valid = true;
            Rectangle bounds = graphics.getBounds();
            // Comprueba si se sale de la pantalla
            if (!bounds.contains(a.getShape().getBounds())) {
                if (a instanceof Enemy) { // Si es un enemigo
                    // Se comprueba con qué eje choca y se rebota sobre él
                    Polygon polygon = (Polygon) a.getShape();
                    if (intersect(new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMinY()), polygon)
                        || intersect(new Line2D.Double(bounds.getMinX(), bounds.getMaxY(), bounds.getMaxX(), bounds.getMaxY()), polygon)) {
                        a.bounce(a.getSpeed(), reflectAngleOnXAxis(a.getDirection())); // Eje x
                    }
                    if (intersect(new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getMinX(), bounds.getMaxY()), polygon)
                        || intersect(new Line2D.Double(bounds.getMaxX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY()), polygon)) {
                        a.bounce(a.getSpeed(), reflectAngleOnYAxis(a.getDirection())); // Eje y
                    }
                } else { // Si no, es el jugador y por tanto pierde
                    gameoverMessage = "You crashed into the wall!";
                    stop();
                }
                valid = false;
            }
            // Comprueba si choca con otro cuerpo
            synchronized (elements) {
                for (ElementType element : elements) { // Para cada tipo de elemento
                    for (ShapeType.Shape b : element.getShapes()) { // Para cada una de sus copias
                        if (!a.equals(b) && intersect(a.getShape(), b.getShape())) { // Si choca
                            if (a instanceof Enemy) {
                                if (b instanceof Enemy) { // Enemigo - enemigo
                                    collision(a, (Unit) b);
                                    valid = false;
                                } else if (b instanceof Food) { // Enemigo - comida
                                    ((Food) b).eat(); // Elimina la comida
                                } else { // Enemigo - serpiente
                                    gameoverMessage = "You were beaten by the " + a.toString() + "!";
                                    stop(); // Termina la partida
                                    valid = false;
                                }
                            } else if (a.getType() instanceof Snake) {
                                if (b instanceof Food) { // Serpiente - comida
                                    Food food = (Food) b;
                                    if (!food.eaten()) { // Puede haberse comido en este ciclo y aún no haber sido eliminada
                                        food.eat(); // La elimina
                                        ((Snake) a.getType()).eat(); // Aumenta la cola
                                    }
                                } else if (b instanceof Enemy) { // Serpiente - enemigo
                                    gameoverMessage = "You were beaten by the " + b.toString() + "!";
                                    stop(); // Termina la partida
                                    valid = false;
                                } else { // Serpiente - cola
                                    gameoverMessage = "You crashed into your own tail!";
                                    stop(); // Termina la partida
                                    valid = false;
                                }
                            }
                        }
                    }
                }
            }
            return valid;
        }

        /**
         * Resuelve una colisión entre 2 cuerpos. Se encarga de hacerlos rebotar
         * de la forma adecuada según las condiciónes físicas.
         *
         * @param a un cuerpo
         * @param b el otro cuerpo
         */
        protected void collision(Unit a, Unit b) {
            // Entre mayor sea un cuerpo respecto al otro,
            // mayor velocidad le imprimirá el choque al otro cuerpo y menor a éste
            float massA = a.getArea();
            float massB = b.getArea();
            float combinedMass = massA + massB;
            float collisionWeightA = 2 * massB / combinedMass;
            float collisionWeightB = 2 * massA / combinedMass;
            float speedA = ((UnitType) a.getType()).getSpeed() * collisionWeightA;
            float speedB = ((UnitType) b.getType()).getSpeed() * collisionWeightB;
            // El ángulo será en dirección opuesta al otro, según la línea que forman sus centros de masa
            a.bounce(speedA, Math.atan2(a.getY() - b.getY(), a.getX() - b.getX()));
            b.bounce(speedB, Math.atan2(b.getY() - a.getY(), b.getX() - a.getX()));
        }
    }

    /**
     * Se encarga de renderizar el juego.
     */
    public class Graphics extends JPanel {

        /**
         * Inicialización.
         */
        public void init() {
            setSize(resolution);
            setMinimumSize(resolution);
            setPreferredSize(resolution);
            setMaximumSize(resolution);
            pack();
        }

        /**
         * Fuerza que el juego empiece o se reanude.
         */
        public void start() {
            Game.this.start();
            Game.this.resume();
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g); // Renderiza el fondo
            Graphics2D g2 = (Graphics2D) g;
            // Establece la calidad del renderizado
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            synchronized (elements) {
                for (ElementType element : elements) {
                    element.paint(g2); // Dibuja cada elemento
                }
            }
            paintFPS(g2);
            g.dispose();
        }

        /**
         * Si está activa la opción dibuja los fps a los que se ejecuta el juego
         * en ese instante.
         */
        private void paintFPS(Graphics2D g2) {
            if (showFPS && !paused) {
                g2.setFont(g2.getFont().deriveFont(32f));
                g2.setColor(ColorUtils.ColorName.getOpposite(background)); // Color contrario al fondo para que siempre se vea bien
                fps.paint(g2, 20, 20 + menu.getHeight());
            }
        }

    }

    /**
     * Asocia cada acción con la tecla que se desee.
     */
    public class KeyBindings {

        private KeyStroke K_UP;
        private KeyStroke K_DOWN;
        private KeyStroke K_RIGHT;
        private KeyStroke K_LEFT;
        private KeyStroke K_ROTATE;
        private KeyStroke K_PAUSE;

        /**
         * Crea una nueva instancia que establece las teclas por defecto.
         */
        public KeyBindings() {
            setK_UP(KeyStroke.getKeyStroke("UP"));
            setK_DOWN(KeyStroke.getKeyStroke("DOWN"));
            setK_RIGHT(KeyStroke.getKeyStroke("RIGHT"));
            setK_LEFT(KeyStroke.getKeyStroke("LEFT"));
            setK_ROTATE(KeyStroke.getKeyStroke("SPACE"));
            setK_PAUSE(KeyStroke.getKeyStroke("ESCAPE"));
        }

        /**
         * Obtiene la tecla asociada a la pausa.
         *
         * @return la tecla asociada a la pausa
         */
        public KeyStroke getK_PAUSE() {
            return K_PAUSE;
        }

        /**
         * Establece la tecla asociada a la pausa.
         *
         * @param K_PAUSE la tecla
         */
        public void setK_PAUSE(KeyStroke K_PAUSE) {
            this.K_PAUSE = K_PAUSE;
            graphics.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(K_PAUSE, "PAUSE");
        }

        /**
         * Obtiene la tecla asociada a rotar.
         *
         * @return the value of ROTATE
         */
        public KeyStroke getK_ROTATE() {
            return K_ROTATE;
        }

        /**
         * Establece la tecla asociada a rotar.
         *
         * @param K_ROTATE la tecla
         */
        public void setK_ROTATE(KeyStroke K_ROTATE) {
            this.K_ROTATE = K_ROTATE;
            graphics.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(K_ROTATE, "ROTATE");
            graphics.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(K_ROTATE.getKeyCode(), 0, true), "ROTATE_RELEASED");
        }

        /**
         * Obtiene la tecla asociada a ir hacia la izquierda.
         *
         * @return la tecla asociada a ir hacia la izquierda
         */
        public KeyStroke getK_LEFT() {
            return K_LEFT;
        }

        /**
         * Establece la tecla asociada a ir hacia la izquierda.
         *
         * @param K_LEFT la tecla
         */
        public void setK_LEFT(KeyStroke K_LEFT) {
            this.K_LEFT = K_LEFT;
            graphics.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(K_LEFT, "LEFT");
        }

        /**
         * Obtiene la tecla asociada a ir hacia la derecha.
         *
         * @return la tecla asociada a ir hacia la derecha
         */
        public KeyStroke getK_RIGHT() {
            return K_RIGHT;
        }

        /**
         * Establece la tecla asociada a ir hacia la derecha
         *
         * @param K_RIGHT la tecla
         */
        public void setK_RIGHT(KeyStroke K_RIGHT) {
            this.K_RIGHT = K_RIGHT;
            graphics.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(K_RIGHT, "RIGHT");
        }

        /**
         * Obtiene la tecla asociada a ir hacia arriba.
         *
         * @return la tecla asociada a ir hacia arriba
         */
        public KeyStroke getK_UP() {
            return K_UP;
        }

        /**
         * Establece la tecla asociada a ir hacia arriba.
         *
         * @param K_UP la tecla
         */
        public void setK_UP(KeyStroke K_UP) {
            this.K_UP = K_UP;
            graphics.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(K_UP, "UP");
        }

        /**
         * Obtiene la tecla asociada a ir hacia abajo.
         *
         * @return la tecla asociada a ir hacia abajo
         */
        public KeyStroke getK_DOWN() {
            return K_DOWN;
        }

        /**
         * Establece la tecla asociada a ir hacia abajo.
         *
         * @param K_DOWN la tecla
         */
        public void setK_DOWN(KeyStroke K_DOWN) {
            this.K_DOWN = K_DOWN;
            graphics.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(K_DOWN, "DOWN");
        }

    }

    /**
     * Tipo de elemento que se compone de un conjunto de figuras geométricas.
     */
    public interface ElementType {

        /**
         * Obtiene todas las figuras que contiene.
         *
         * @return las copias
         */
        public Iterable<ShapeType.Shape> getShapes();

        /**
         * Restablece todos sus elementos.
         */
        public void init();

        /**
         * Actualiza todos sus elementos.
         *
         * @param deltaTime el tiempo que transcurrió desde la última
         * actualización, en segundos
         */
        public void update(float deltaTime);

        /**
         * Dibuja todos sus elementos.
         *
         * @param g los gráficos en los que se dibujarán
         */
        public void paint(Graphics2D g);

        /**
         * Obtiene la GUI que modifica los atributos del elemento.
         *
         * @return la GUI
         */
        public ExtensiblePanel getUI();

    }

}

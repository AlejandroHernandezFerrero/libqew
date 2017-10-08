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

import game.Game.ElementType;
import game.Game.Physics;
import game.ShapeType.Shape;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import libqew.ExtensiblePanel;
import ui.EnemiesUI;
import utils.ColorUtils.ColorName;
import utils.RandomUtils.Random;
import utils.RandomUtils.RandomBoolean;
import utils.RandomUtils.RandomGaussian;

/**
 * Se encarga de crear y gestionar un grupo de enemigos. Permite generar
 * enemigos automáticamente y de forma aleatoria a partir de unos parámetros
 * estadísticos configurables.
 *
 * @author Alejandro Hernández Ferrero
 */
public class Enemies implements ElementType {

    private final Physics physics;
    private final Snake snake;
    private Generator generator;
    private ArrayList<EnemyType> enemies;

    /**
     * Crea un grupo de enemigos que seguirán a un objetivo.
     *
     * @param physics las leyes físicas del juego
     * @param snake el objetivo
     */
    public Enemies(Physics physics, Snake snake) {
        this.physics = physics;
        this.snake = snake;
        enemies = new ArrayList<>();
        generator = new Generator();
        generator.generate(enemies);
    }

    @Override
    public void init() {
        for (EnemyType enemy : enemies) {
            enemy.init();
        }
    }

    @Override
    public void update(float deltaTime) {
        for (EnemyType enemy : enemies) {
            enemy.update(deltaTime);
        }
    }

    @Override
    public void paint(Graphics2D g) {
        for (EnemyType enemy : enemies) {
            enemy.paint(g);
        }
    }

    /**
     * Obtiene todos los enemigos que contiene.
     *
     * @return los enemigos que contiene
     */
    public ArrayList<EnemyType> getEnemies() {
        return enemies;
    }

    /**
     * Establece su conjunto de enemigos. Pierde los que tenía previamente.
     *
     * @param enemies la nueva lista de enemigos
     */
    public void setEnemies(ArrayList<EnemyType> enemies) {
        this.enemies = enemies;
    }

    @Override
    public ExtensiblePanel getUI() {
        return new EnemiesUI(this);
    }

    @Override
    public Iterable<Shape> getShapes() {
        // Recorre cada copia de cada tipo de enemigo que contiene esta clase
        return new Iterable<Shape>() {
            @Override
            public Iterator<Shape> iterator() {
                return new Iterator<Shape>() {
                    {
                        nextIndex(); // Se inicializa buscando el primer índice
                    }

                    private int index; // �?ndice del tipo de enemigo
                    private int subIndex; // �?ndice de las copias dentro del tipo

                    @Override
                    public boolean hasNext() {
                        return (index < enemies.size());
                    }

                    @Override
                    public Shape next() {
                        Shape shape = enemies.get(index).getShapes().get(subIndex);
                        subIndex++;
                        nextIndex();
                        return shape;
                    }

                    /**
                     * Deja los índices en la siguiente posición válida si no lo
                     * son
                     */
                    private void nextIndex() {
                        while (hasNext() && subIndex >= enemies.get(index).getShapes().size()) {
                            subIndex = 0;
                            index++;
                        }
                    }
                };
            }
        };

    }

    /**
     * Obtiene el generador de enemigos que usa esta clase.
     *
     * @return el generador de enemigos
     */
    public Generator getGenerator() {
        return generator;
    }

    /**
     * Establece el generador de enemigos de esta clase.
     *
     * @param generator el generador de enemigos
     */
    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    /**
     * Encapsula una serie de parámetros estadísticos configurables y permite
     * generar un conjunto de enemigos a partir de ellos.
     */
    public class Generator {

        private RandomGaussian count;
        private Random hue;
        private RandomGaussian fillSaturation;
        private RandomGaussian fillLuminance;
        private RandomGaussian borderSaturation;
        private RandomGaussian borderLuminance;
        private RandomGaussian sides;
        private RandomGaussian radius;
        private RandomGaussian speed;
        private RandomGaussian turningSpeed;
        private RandomGaussian angularSpeed;
        private RandomGaussian copies;
        private RandomBoolean border;
        private RandomBoolean fill;
        private RandomBoolean rotate;
        private RandomBoolean clockwise;

        /**
         * Crea un nuevo generador.
         */
        public Generator() {
            count = new RandomGaussian(20, 1, 18, 22);
            hue = new Random(0, 1);
            fillSaturation = new RandomGaussian(0.8f, 0.5f, 0, 1);
            fillLuminance = new RandomGaussian(0.9f, 0.2f, 0, 1);
            borderSaturation = new RandomGaussian(0.6f, 0.2f, 0, 1);
            borderLuminance = new RandomGaussian(0.6f, 0.1f, 0, 1);
            sides = new RandomGaussian(7, 4, 3, 8);
            radius = new RandomGaussian(8, 2, 6, 50);
            speed = new RandomGaussian(0.05f, 0.03f, 0.01f, 0.2f);
            turningSpeed = new RandomGaussian(0.35f, 0.05f, 0.3f, 0.5f);
            angularSpeed = new RandomGaussian(0.3f, 0.2f, 0.01f, 50);
            copies = new RandomGaussian(1.5f, 0.1f, 1, 2);
            fill = new RandomBoolean(1);
            border = new RandomBoolean(1);
            rotate = new RandomBoolean(0.2f);
            clockwise = new RandomBoolean(0.5f);
        }

        /**
         * Crea un nuevo generador que copiará todos sus parámetros de otro.
         *
         * @param copy el generador a copiar
         */
        public Generator(Generator copy) {
            this.count = copy.count.copy();
            this.hue = copy.hue.copy();
            this.fillSaturation = copy.fillSaturation.copy();
            this.fillLuminance = copy.fillLuminance.copy();
            this.borderSaturation = copy.borderSaturation.copy();
            this.borderLuminance = copy.borderLuminance.copy();
            this.sides = copy.sides.copy();
            this.radius = copy.radius.copy();
            this.speed = copy.speed.copy();
            this.turningSpeed = copy.turningSpeed.copy();
            this.angularSpeed = copy.angularSpeed.copy();
            this.copies = copy.copies.copy();
            this.border = copy.border.copy();
            this.fill = copy.fill.copy();
            this.rotate = copy.rotate.copy();
            this.clockwise = copy.clockwise.copy();
        }

        /**
         * Obtiene un nuevo generador con todos sus parámetros idénticos a los
         * de éste.
         *
         * @return el nuevo generador
         */
        public Generator copy() {
            return new Generator(this);
        }

        /**
         * Crea un nuevo enemigo con los parámetros actuales.
         *
         * @return el nuevo enemigo
         */
        public EnemyType createRandomEnemy() {
            EnemyType enemy = new EnemyType(physics, snake);
            float h = hue.generate(); // Se usa tanto para el interior como para el borde
            enemy.setFilled(fill.generate());
            enemy.setColor(new ColorName(Color.HSBtoRGB(h, fillSaturation.generate(), fillLuminance.generate())));
            enemy.setBorder(border.generate());
            enemy.setBorderColor(new ColorName(Color.HSBtoRGB(h, borderSaturation.generate(), borderLuminance.generate())));
            enemy.setSides((int) sides.generate());
            enemy.setRadius((int) radius.generate());
            enemy.setSpeed(speed.generate() * enemy.getRadius() * enemy.getRadius());
            enemy.setTurningSpeed(turningSpeed.generate());
            enemy.setCopies((int) copies.generate());
            enemy.setRotation(rotate.generate());
            enemy.setAngularSpeed(angularSpeed.generate());
            enemy.setClockwise(clockwise.generate());
            return enemy;
        }

        /**
         * Genera un grupo de enemigos. El tamaño del grupo depende del
         * parámetro {@link #getCount}.
         *
         * @param list el grupo generado
         */
        public void generate(ArrayList<EnemyType> list) {
            int n = (int) count.generate();
            for (int i = 0; i < n; i++) {
                list.add(createRandomEnemy());
            }
        }

        /**
         * Obtiene el generador del tamaño del grupo a generar.
         *
         * @return el tamaño del grupo
         */
        public RandomGaussian getCount() {
            return count;
        }

        /**
         * Establece el generador del tamaño del grupo a generar.
         *
         * @param count el generador del tamaño del grupo
         */
        public void setCount(RandomGaussian count) {
            this.count = count;
        }

        /**
         * Obtiene el generador del matiz del color.
         *
         * @return el generador del matiz del color
         */
        public Random getHue() {
            return hue;
        }

        /**
         * Establece el generador del matiz del color.
         *
         * @param hue el generador del matiz del color
         */
        public void setHue(Random hue) {
            this.hue = hue;
        }

        /**
         * Obtiene el generador de la saturación del relleno.
         *
         * @return el generador de la saturación del relleno
         */
        public RandomGaussian getFillSaturation() {
            return fillSaturation;
        }

        /**
         * Establece el generador de la saturación del relleno.
         *
         * @param fillSaturation el generador de la saturación del relleno
         */
        public void setFillSaturation(RandomGaussian fillSaturation) {
            this.fillSaturation = fillSaturation;
        }

        /**
         * Obtiene el generador de la luminancia del relleno.
         *
         * @return el generador de la luminancia del relleno
         */
        public RandomGaussian getFillLuminance() {
            return fillLuminance;
        }

        /**
         * Establece el generador de la luminancia del relleno.
         *
         * @param fillLuminance el generador de
         */
        public void setFillLuminance(RandomGaussian fillLuminance) {
            this.fillLuminance = fillLuminance;
        }

        /**
         * Obtiene el generador de la saturación del borde.
         *
         * @return el generador de la saturación del borde
         */
        public RandomGaussian getBorderSaturation() {
            return borderSaturation;
        }

        /**
         * Establece el generador de la saturación del borde.
         *
         * @param borderSaturation el generador de la saturación del borde
         */
        public void setBorderSaturation(RandomGaussian borderSaturation) {
            this.borderSaturation = borderSaturation;
        }

        /**
         * Obtiene el generador de la luminancia del borde.
         *
         * @return el generador de la luminancia del borde
         */
        public RandomGaussian getBorderLuminance() {
            return borderLuminance;
        }

        /**
         * Establece el generador de la luminancia del borde.
         *
         * @param borderLuminance el generador de la luminancia del borde
         */
        public void setBorderLuminance(RandomGaussian borderLuminance) {
            this.borderLuminance = borderLuminance;
        }

        /**
         * Obtiene el generador del número de lados.
         *
         * @return el generador del número de lados
         */
        public RandomGaussian getSides() {
            return sides;
        }

        /**
         * Establece el generador del número de lados.
         *
         * @param sides el generador del número de lados
         */
        public void setSides(RandomGaussian sides) {
            this.sides = sides;
        }

        /**
         * Obtiene el generador del radio.
         *
         * @return el generador del radio
         */
        public RandomGaussian getRadius() {
            return radius;
        }

        /**
         * Establece el generador del radio.
         *
         * @param radius el generador del radio
         */
        public void setRadius(RandomGaussian radius) {
            this.radius = radius;
        }

        /**
         * Obtiene el generador de la velocidad.
         *
         * @return el generador de la velocidad
         */
        public RandomGaussian getSpeed() {
            return speed;
        }

        /**
         * Establece el generador de la velocidad.
         *
         * @param speed el generador de la velocidad
         */
        public void setSpeed(RandomGaussian speed) {
            this.speed = speed;
        }

        /**
         * Obtiene el generador de la velocidad de giro.
         *
         * @return el generador de la velocidad de giro
         */
        public RandomGaussian getTurningSpeed() {
            return turningSpeed;
        }

        /**
         * Establece el generador de la velocidad de giro.
         *
         * @param turningSpeed el generador de la velocidad de giro
         */
        public void setTurningSpeed(RandomGaussian turningSpeed) {
            this.turningSpeed = turningSpeed;
        }

        /**
         * Obtiene el generador de la velocidad angular.
         *
         * @return el generador de la velocidad angular
         */
        public RandomGaussian getAngularSpeed() {
            return angularSpeed;
        }

        /**
         * Establece el generador de la velocidad angular.
         *
         * @param angularSpeed el generador de la velocidad angular
         */
        public void setAngularSpeed(RandomGaussian angularSpeed) {
            this.angularSpeed = angularSpeed;
        }

        /**
         * Obtiene el generador del número de copias de este tipo que se
         * generarán.
         *
         * @return el generador del número de copias
         */
        public RandomGaussian getCopies() {
            return copies;
        }

        /**
         * Establece el generador del número de copias de este tipo que se
         * generarán.
         *
         * @param copies el generador del número de copias
         */
        public void setCopies(RandomGaussian copies) {
            this.copies = copies;
        }

        /**
         * Obtiene la probabilidad de que tenga borde.
         *
         * @return la probabilidad de que tenga borde
         */
        public RandomBoolean getBorder() {
            return border;
        }

        /**
         * Establece la probabilidad de que tenga borde.
         *
         * @param border la probabilidad de que tenga borde
         */
        public void setBorder(RandomBoolean border) {
            this.border = border;
        }

        /**
         * Obtiene probabilidad de que tenga relleno.
         *
         * @return la probabilidad de que tenga relleno
         */
        public RandomBoolean getFill() {
            return fill;
        }

        /**
         * Establece la probabilidad de que tenga relleno.
         *
         * @param fill la probabilidad de que tenga relleno
         */
        public void setFill(RandomBoolean fill) {
            this.fill = fill;
        }

        /**
         * Obtiene la probabilidad de que tenga rotación continua.
         *
         * @return la probabilidad de que tenga rotación continua
         */
        public RandomBoolean getRotate() {
            return rotate;
        }

        /**
         * Establece la probabilidad de que tenga rotación continua.
         *
         * @param rotate la probabilidad de que tenga rotación continua
         */
        public void setRotate(RandomBoolean rotate) {
            this.rotate = rotate;
        }

        /**
         * Obtiene la probabilidad de que gire en sentido horario.
         *
         * @return la probabilidad de que gire en sentido horario
         */
        public RandomBoolean getClockwise() {
            return clockwise;
        }

        /**
         * Establece la probabilidad de que gire en sentido horario.
         *
         * @param clockwise la probabilidad de que gire en sentido horario
         */
        public void setClockwise(RandomBoolean clockwise) {
            this.clockwise = clockwise;
        }

    }

}

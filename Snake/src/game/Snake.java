/*
 * Copyright (C) 2017 Alejandro Hernández Ferrero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package game;

import game.Game.Graphics;
import game.Snake.Tail;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import libqew.ExtensiblePanel;
import libqew.TabsPanel;
import ui.SnakeUI;
import utils.GeometryUtils;

/**
 * Define las propiedades de la serpiente y contiene todos sus fragmentos.
 *
 * @author Alejandro Hernández Ferrero
 */
public class Snake extends UnitType {

    private int tailSides = 4;
    private int tailInitialAngle = 45;

    private int eaten;
    private final Graphics graphics;
    private float time;
    private float delta;

    /**
     * Crea una nueva serpiente.
     *
     * @param physics las leyes físicas
     * @param graphics los gráficos que recibirán las pulsaciones de las teclas
     */
    public Snake(Game.Physics physics, Game.Graphics graphics) {
        super(physics);
        this.graphics = graphics;
    }

    @Override
    public void init() {
        super.init();
        new Head(graphics);
        delta = getRadius() * 2 / getSpeed();
    }

    /**
     * Obtiene la cabeza de esta serpiente.
     *
     * @return la cabeza
     */
    public Head getHead() {
        return (Head) getShapes().get(0);
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        while (time >= delta) { // No debe moverse de forma continua
            time -= delta;
            // Se añade un fragmento por cada comida
            while (eaten > 0 && getHead().isMoving()) {
                Unit tail = (Unit) getShapes().get(getShapes().size() - 1); // Último fragmento
                double angle = GeometryUtils.oppositeAngle(tail.getDirection());
                float x = (float) (tail.getX() + getRadius() * 2 * Math.cos(angle));
                float y = (float) (tail.getY() + getRadius() * 2 * Math.sin(angle));
                new Tail(x, y).setDirection(tail.getDirection());
                eaten--;
            }
            for (int i = getShapes().size() - 1; i > 0; i--) { // Propaga las direcciones
                ((Tail) getShapes().get(i)).setDirection(((Unit) getShapes().get(i - 1)).getDirection());
            }
            getHead().updateDirection(); // Actualiza la direccion de la cabeza a la de la última tecla pulsada
            super.update(delta);
        }
    }

    /**
     * Ocasiona que aumente su cola en un fragmento.
     */
    public void eat() {
        eaten++;
    }

    @Override
    public ExtensiblePanel getUI() {
        TabsPanel ui = (TabsPanel) super.getUI();
        ((ExtensiblePanel) ui.getTab(1)).addChild(new SnakeUI(this));
        ui.setName("Snake");
        return ui;
    }

    /**
     * Obtiene la rotación inicial que tienen los fragmentos de la cola.
     *
     * @return el ángulo de rotación, en grados
     */
    public int getTailInitialAngle() {
        return tailInitialAngle;
    }

    /**
     * Establece la rotación inicial que tienen los fragmentos de la cola.
     *
     * @param tailInitialAngle el ángulo de rotación, en grados
     */
    public void setTailInitialAngle(int tailInitialAngle) {
        this.tailInitialAngle = tailInitialAngle;
    }

    /**
     * Obtiene los lados de los fragmentos de la cola.
     *
     * @return los lados de los fragmentos de la cola
     */
    public int getTailSides() {
        return tailSides;
    }

    /**
     * Establece los lados de los fragmentos de la cola.
     *
     * @param tailSides los lados de los fragmentos de la cola
     */
    public void setTailSides(int tailSides) {
        this.tailSides = tailSides;
    }

    /**
     * Cabeza de la serpiente.
     */
    public class Head extends Unit {

        private boolean moving;
        private boolean rotating;
        private double angle;
        private double direction;

        /**
         * Crea la cabeza de la serpiente.
         *
         * @param graphics los gráficos que recibirán las pulsaciones de las
         * teclas
         */
        public Head(Graphics graphics) {
            super(graphics.getBounds().width / 2 - getRadius(), graphics.getBounds().height / 2 - getRadius()); // Empieza en el centro
            // Enlaza cada tecla con los movimientos de la serpiente
            graphics.getActionMap().put("UP", new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        moving = true;
                                        angle = Math.PI * 1.5f;
                                        graphics.start();
                                    }
                                });
            graphics.getActionMap().put("DOWN", new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        moving = true;
                                        angle = Math.PI / 2f;
                                        graphics.start();
                                    }
                                });
            graphics.getActionMap().put("RIGHT", new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        moving = true;
                                        angle = 0;
                                        graphics.start();
                                    }
                                });
            graphics.getActionMap().put("LEFT", new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        moving = true;
                                        angle = Math.PI;
                                        graphics.start();
                                    }
                                });
            graphics.getActionMap().put("ROTATE", new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        rotating = true;
                                    }
                                });
            graphics.getActionMap().put("ROTATE_RELEASED", new AbstractAction() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        rotating = false;
                                    }
                                });

        }

        /**
         * Actualiza la direccion a la de la última tecla pulsada.
         */
        private void updateDirection() {
            direction = angle;
        }

        @Override
        public boolean isMoving() {
            return moving;
        }

        @Override
        public boolean isRotating() {
            return rotating;
        }

        @Override
        public double getDirection() {
            return direction;
        }

    }

    /**
     * Un fragmento de la cola de la serpiente.
     */
    public class Tail extends Unit {

        private double direction;

        /**
         * Crea un nuevo fragmento de cola en las coordinadas.
         *
         * @param x la coordinada x
         * @param y la coordinada y
         */
        public Tail(float x, float y) {
            super(x, y, tailSides, tailInitialAngle);
            setAngle(getHead().getAngle()); // Sincroniza la rotación de la serpiente
        }

        @Override
        public boolean isMoving() {
            return getHead().isMoving();
        }

        @Override
        public boolean isRotating() {
            return getHead().isRotating();
        }

        @Override
        public double getDirection() {
            return direction;
        }

        /**
         * Establece la dirección del fragmento.
         */
        private void setDirection(double direction) {
            this.direction = direction;
        }
    }

}

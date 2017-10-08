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

import java.awt.Polygon;
import libqew.ExtensiblePanel;
import libqew.StackPanel;
import libqew.TabsPanel;
import ui.UnitUI;
import utils.GeometryUtils;

/**
 * Define las propiedades de un tipo de unidad y contiene todas las unidades de
 * este tipo. Se caracteriza por tener forma poligonal y tener movimiento.
 *
 * @author Alejandro Hernández Ferrero
 */
public class UnitType extends ShapeType {

    private float speed = 200;
    private int sides = 7;
    private boolean rotate = true;
    private float angularSpeed = (float) (Math.PI * 0.5f);
    private boolean clockwise = true;
    private int initialAngle = 0;

    /**
     * Crea un nuevo tipo de unidad.
     *
     * @param physics las leyes físicas del juego
     */
    public UnitType(Game.Physics physics) {
        super(physics);
    }

    @Override
    public ExtensiblePanel getUI() {
        ExtensiblePanel shapeui = super.getUI();
        TabsPanel ui = new TabsPanel();
        ui.addChild(shapeui);
        StackPanel unitui = new StackPanel(new UnitUI(this));
        ui.addChild(unitui);
        return ui;
    }

    /**
     * Obtiene la rotación inicial.
     *
     * @return el ángulo de rotación, en grados
     */
    public int getInitialAngle() {
        return initialAngle;
    }

    /**
     * Establece la rotación inicial.
     *
     * @param initialAngle el ángulo de rotación, en grados
     */
    public void setInitialAngle(int initialAngle) {
        this.initialAngle = initialAngle;
    }

    /**
     * Comprueba si gira en sentido horario.
     *
     * @return si gira en sentido horario
     */
    public boolean isClockwise() {
        return clockwise;
    }

    /**
     * Establece si gira en sentido horario.
     *
     * @param clockwise si gira en sentido horario
     */
    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    /**
     * Obtiene la velocidad angular.
     *
     * @return la velocidad angular, en revoluciones por segundo
     */
    public float getAngularSpeed() {
        return angularSpeed;
    }

    /**
     * Establece la velocidad angular.
     *
     * @param angularSpeed la velocidad angular, en revoluciones por segundo
     */
    public void setAngularSpeed(float angularSpeed) {
        this.angularSpeed = angularSpeed;
    }

    /**
     * Comprueba si tiene rotación.
     *
     * @return si tiene rotación
     */
    public boolean hasRotation() {
        return rotate;
    }

    /**
     * Establece si tiene rotación.
     *
     * @param rotate si tiene rotación.
     */
    public void setRotation(boolean rotate) {
        this.rotate = rotate;
    }

    /**
     * Obtiene el número de lados.
     *
     * @return el número de lados
     */
    public int getSides() {
        return sides;
    }

    /**
     * Establece el número de lados.
     *
     * @param sides el número de lados
     */
    public void setSides(int sides) {
        this.sides = sides;
    }

    /**
     * Obtiene la velocidad.
     *
     * @return la velocidad, en píxeles por segundo
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Establece la velocidad.
     *
     * @param speed la velocidad, en píxeles por segundo
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Unidad particular con las características comunes de este tipo.
     */
    public class Unit extends ShapeType.Shape {

        private Polygon polygon;
        private float[] xvertices;
        private float[] yvertices;
        private double angle;

        private int[][] xpoints;
        private int[][] ypoints;
        private int pos;

        /**
         * Crea una nueva unidad en las coordenadas.
         *
         * @param x la coordenada x
         * @param y la coordenada y
         */
        public Unit(float x, float y) {
            super(x, y);
            createPolygon(getSides(), getInitialAngle());
        }

        /**
         * Crea una nueva unidad en una posición aleatoria.
         */
        public Unit() {
            super();
            createPolygon(getSides(), getInitialAngle());
        }

        /**
         * Crea una nueva unidad en las coordenadas y con el número de lados y
         * la rotación inicial especificada.
         *
         * @param x la coordenada x
         * @param y la coordenada y
         * @param sides el número de lados
         * @param initialAngle el ángulo de rotación inicial
         */
        public Unit(float x, float y, int sides, int initialAngle) {
            super(x, y);
            createPolygon(sides, initialAngle);
        }

        /**
         * Inicializa los datos del polígono.
         */
        private void createPolygon(int sides, int initialAngle) {
            polygon = new Polygon();
            // Calcula los vértices originales
            // Se mantiene una copia de los puntos originales y no se modifican
            // Los puntos del polígono real se calculan en todo momento a partir de los originales
            // De esta forma se evita deformar la figura debido al acarreo de imprecisión
            xvertices = new float[sides];
            yvertices = new float[sides];
            double theta = 2 * Math.PI / sides;
            double offset = Math.toRadians(-initialAngle % 360);
            for (int i = 0; i < sides; i++) {
                xvertices[i] = (float) (Math.cos(theta * i + offset) * getRadius());
                yvertices[i] = (float) (Math.sin(theta * i + offset) * getRadius());
            }
            polygon.npoints = sides;
            // Estos puntos sí serán los reales en cada momento
            // Están en doble página para poder revertir el último cambio
            xpoints = new int[2][sides];
            ypoints = new int[2][sides];
            swap();
            // Los inicializa con la posición actual
            for (int i = 0; i < sides; i++) {
                polygon.xpoints[i] = Math.round(xvertices[i] + getX());
                polygon.ypoints[i] = Math.round(yvertices[i] + getY());
            }
            polygon.invalidate(); // Se ha cambiado el polígono internamente
        }

        /**
         * Cambia la página en uso.
         */
        private void swap() {
            pos = ++pos % 2;
            polygon.xpoints = xpoints[pos];
            polygon.ypoints = ypoints[pos];
        }

        /**
         * Mueve esta unidad durante el periodo de tiempo. Si el resultado lleva
         * a la unidad a un estado no válido no se efectuará el movimiento.
         *
         * @param deltaTime el periodo de tiempo, en segundos
         * @return <code>true</code> si se efectuó el movimiento
         */
        public boolean move(float deltaTime) {
            double oldangle = angle; // Por si hay que revertir
            float x = getX();
            float y = getY();
            if (rotate) { // Si tiene rotación
                if (isRotating()) { // Y está rotando en este momento
                    double delta = deltaTime * angularSpeed * 2 * Math.PI; // �?ngulo que rota en este tiempo
                    delta = clockwise ? delta : -delta; // Ajusta el sentido
                    angle = (angle + delta) % (2 * Math.PI); // Nuevo ángulo
                }
            } else if (!isBouncing()) { // Si no y si no está rebotando
                angle = getDirection(); // El ángulo será su dirección
            }
            if (!isMoving() && angle == oldangle) { // Si no cambia nada no hace falta recalcular
                return true;
            }
            if (isMoving()) { // Si se movió se recalcula la posición
                float speed = getSpeed();
                float deltaX = (float) (speed * deltaTime * Math.cos(getDirection()));
                float deltaY = (float) (speed * deltaTime * Math.sin(getDirection()));
                x += deltaX;
                y += deltaY;
            }
            // Se cambia de página y se escriben los nuevos valores en ella
            swap();
            float cosAngle = (float) Math.cos(angle);
            float sinAngle = (float) Math.sin(angle);
            for (int i = 0; i < xvertices.length; i++) {
                polygon.xpoints[i] = Math.round(x + xvertices[i] * cosAngle - yvertices[i] * sinAngle);
                polygon.ypoints[i] = Math.round(y + xvertices[i] * sinAngle + yvertices[i] * cosAngle);
            }
            polygon.invalidate(); // Se le avisa de que se cambió
            if (!physics().validatePosition(this)) { // Si no es válido
                // Hay que revertir todos los cambios
                swap(); // Cambia a la otra página, que contiene el estado anterior
                angle = oldangle; // Revierte el ángulo
                return false;
            } else {
                setX(x);
                setY(y);
                return true;
            }
        }

        @Override
        public java.awt.Shape getShape() {
            return polygon;
        }

        @Override
        public void update(float deltaTime) {
            move(deltaTime);
        }

        /**
         * Provoca que esta unidad rebote.
         *
         * @param speed la velocidad que tendrá mientras rebota
         * @param angle la dirección que seguirá mientras rebota
         */
        public void bounce(float speed, double angle) {

        }

        /**
         * Obtiene el área que ocupa la figura.
         *
         * @return el área
         */
        public float getArea() {
            return GeometryUtils.getArea(polygon);
        }

        /**
         * Comprueba si se está moviendo en este momento.
         *
         * @return <code>true</code> si se está moviendo
         */
        public boolean isMoving() {
            return false;
        }

        /**
         * Comprueba si está rotando en este momento.
         *
         * @return <code>true</code> si está rotando
         */
        public boolean isRotating() {
            return false;
        }

        /**
         * Comprueba si está rebotando en este momento.
         *
         * @return <code>true</code> si está rebotando
         */
        public boolean isBouncing() {
            return false;
        }

        /**
         * Obtiene la dirección que sigue en este momento.
         *
         * @return la dirección
         */
        public double getDirection() {
            return 0;
        }

        /**
         * Obtiene la velocidad que tiene en este momento.
         *
         * @return la velocidad
         */
        public float getSpeed() {
            return speed;
        }

        /**
         * Obtiene el ángulo de rotación que tiene la figura en este momento.
         *
         * @return el ángulo
         */
        public double getAngle() {
            return angle;
        }

        /**
         * Establece el ángulo de rotación de la figura.
         *
         * @param angle el ángulo
         */
        public void setAngle(double angle) {
            this.angle = angle;
        }

    }

}

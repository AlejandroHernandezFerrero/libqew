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

import game.Game.ElementType;
import game.Game.Physics;
import game.ShapeType.Shape;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import libqew.ExtensiblePanel;
import libqew.StackPanel;
import ui.ShapeUI;
import utils.ColorUtils;

/**
 * Define las propiedades de un tipo de figura geométrica y contiene todas las
 * figuras de este tipo.
 *
 * @author Alejandro Hernández Ferrero
 */
public abstract class ShapeType implements ElementType {

    private static int ids;

    private ArrayList<Shape> elements = new ArrayList<>();
    private Physics physics;
    private int id = ++ids;

    private Color color = new ColorUtils.ColorName(0, 153, 102, 255);
    private boolean filled = true;
    private int radius = 10;
    private boolean border = true;
    private Color borderColor = new ColorUtils.ColorName(0, 102, 51, 255);
    private int borderWidth = 2;

    /**
     * Crea un nuevo tipo de figura geométrica.
     *
     * @param physics las leyes físicas del juego.
     */
    public ShapeType(Physics physics) {
        this.physics = physics;
    }

    /**
     * Obtiene las leyes físicas del juego.
     *
     * @return las leyes físicas del juego
     */
    protected Physics physics() {
        return physics;
    }

    @Override
    public ArrayList<Shape> getShapes() {
        return elements;
    }

    @Override
    public void update(float deltaTime) {
        for (Shape element : getShapes()) {
            element.update(deltaTime);
        }
    }

    @Override
    public void paint(Graphics2D g) {
        for (Shape element : getShapes()) {
            element.paint(g);
        }
    }

    @Override
    public void init() {
        elements.clear();
    }

    @Override
    public ExtensiblePanel getUI() {
        return new StackPanel(new ShapeUI(this));
    }

    /**
     * Obtiene un número que identifica inequívocamente a cada instancia.
     *
     * @return el identificador
     */
    public int id() {
        return id;
    }

    /**
     * Obtiene la anchura del borde.
     *
     * @return la anchura del borde
     */
    public int getBorderWidth() {
        return borderWidth;
    }

    /**
     * Establece la anchura del borde.
     *
     * @param borderWidth la anchura del borde
     */
    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    /**
     * Obtiene el color del borde.
     *
     * @return el color del borde
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Establece el color del borde.
     *
     * @param borderColor el color del borde
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Comprueba si tiene borde.
     *
     * @return si tiene borde
     */
    public boolean hasBorder() {
        return border;
    }

    /**
     * Establece si tiene borde.
     *
     * @param border si tiene borde
     */
    public void setBorder(boolean border) {
        this.border = border;
    }

    /**
     * Obtiene el radio.
     *
     * @return el radio
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Establece el radio.
     *
     * @param radius el radio
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * Comprueba si tiene relleno.
     *
     * @return si tiene relleno
     */
    public boolean isFilled() {
        return filled;
    }

    /**
     * Establece si tiene relleno.
     *
     * @param filled si tiene relleno
     */
    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    /**
     * Obtiene el color.
     *
     * @return el color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Establece el color.
     *
     * @param color el color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Figura geométrica particular con las características comunes de este
     * tipo.
     */
    public abstract class Shape {

        private float x;
        private float y;

        /**
         * Crea una nueva figura geométrica en unas coordenadas.
         *
         * @param x la coordenada x
         * @param y la coordenada y
         */
        public Shape(float x, float y) {
            this.x = x;
            this.y = y;
            getShapes().add(this);
        }

        /**
         * Crea una nueva figura geométrica en una posición aleatoria.
         */
        public Shape() {
            Point point = physics.generateRandomPosition(radius); // Pide una posición libre
            if (point != null) { // Solo se añade si se encontró una posición libre
                x = point.x;
                y = point.y;
                getShapes().add(this);
            }
        }

        /**
         * Dibuja este elemento.
         *
         * @param g los gráficos en los que dibujarlo
         */
        public void paint(Graphics2D g) {
            if (filled) {
                g.setColor(color);
                g.fill(getShape());
            }
            if (border) {
                g.setStroke(new BasicStroke(borderWidth));
                g.setColor(borderColor);
                g.draw(getShape());
            }
        }

        /**
         * Obtiene la figura geométrica de este elemento.
         *
         * @return la figura geométrica
         */
        public abstract java.awt.Shape getShape();

        /**
         * Obtiene el tipo de elemento al que pertenece.
         *
         * @return el tipo de elemento
         */
        public ShapeType getType() {
            return ShapeType.this;
        }

        /**
         * Obtiene la coordenada x.
         *
         * @return la coordenada x
         */
        protected float getX() {
            return x;
        }

        /**
         * Establece la coordenada x.
         *
         * @param x la coordenada x
         */
        protected void setX(float x) {
            this.x = x;
        }

        /**
         * Obtiene la coordenada y.
         *
         * @return la coordenada y
         */
        protected float getY() {
            return y;
        }

        /**
         * Establece la coordenada y.
         *
         * @param y la coordenada y
         */
        protected void setY(float y) {
            this.y = y;
        }

        /**
         * Actualiza este elemento.
         *
         * @param deltaTime el tiempo que transcurrió desde la última
         * actualización, en segundos
         */
        public void update(float deltaTime) {

        }

    }

}

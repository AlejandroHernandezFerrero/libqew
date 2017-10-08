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

import game.Game.Physics;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import libqew.ExtensiblePanel;
import ui.FoodUI;
import utils.ColorUtils.ColorName;

/**
 * Define las propiedades de un tipo de comida y contiene toda la comida de este
 * tipo. Se caracteriza por tener forma circular y no tener movimiento.
 *
 * @author Alejandro Hernández Ferrero
 */
public class FoodType extends ShapeType {

    private int maximum = 8;
    private float spawnTime = 3;

    private float time;
    private final ArrayList<Food> delete = new ArrayList<>();

    /**
     * Crea un nuevo tipo de comida.
     *
     * @param physics las leyes físicas del juego
     */
    public FoodType(Physics physics) {
        super(physics);
        // Valores por defecto
        setColor(ColorName.Gold);
        setRadius(12);
        setBorderColor(new ColorName(50, 50, 0, 255));
    }

    @Override
    public void init() {
        super.init();
        delete.clear();
        time = spawnTime;
    }

    @Override
    public ExtensiblePanel getUI() {
        ExtensiblePanel ui = super.getUI();
        ui.addChild(new FoodUI(this));
        ui.setName("Food");
        return ui;
    }

    /**
     * Obtiene el tiempo en segundos que tarda en aparecer cada copia.
     *
     * @return el tiempo que tarda en aparecer cada copia, en segundos
     */
    public float getSpawnTime() {
        return spawnTime;
    }

    /**
     * Establece el tiempo en segundos que tarda en aparecer cada copia.
     *
     * @param spawnTime el tiempo que tarda en aparecer cada copia, en segundos
     */
    public void setSpawnTime(float spawnTime) {
        this.spawnTime = spawnTime;
    }

    /**
     * Obtiene el número máximo de copias que puede haber al mismo tiempo.
     *
     * @return el número máximo de copias
     */
    public int getMaximum() {
        return maximum;
    }

    /**
     * Establece el número máximo de copias que puede haber al mismo tiempo.
     *
     * @param maximum el número máximo de copias
     */
    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    @Override
    public void update(float deltaTime) {
        getShapes().removeAll(delete); // Borra las pendientes
        delete.clear();
        if (getShapes().size() < maximum) { // Solo si no hay ya el máximo número de copias
            time += deltaTime;
            while (time >= spawnTime && getShapes().size() < maximum) { // Mientras que haya pasado suficiente tiempo para que aparezca otra
                time -= spawnTime;
                new Food();
            }
        }
    }

    /**
     * En vez de borrarse directamente se marcan como pendientes y se hace en el
     * siguiente ciclo. De esta forma se evitan conflictos al modificar la lista
     * mientras se está iterando.
     */
    private void delete(Food food) {
        delete.add(food);
    }

    /**
     * Comida particular con las características comunes de este tipo.
     */
    public class Food extends Shape {

        private final Ellipse2D.Float circle = new Ellipse2D.Float();
        private boolean eaten;

        /**
         * Crea una nueva comida.
         */
        public Food() {
            super();
            circle.x = getX() - getRadius();
            circle.y = getY() - getRadius();
            circle.width = getRadius() * 2;
            circle.height = getRadius() * 2;
        }

        @Override
        public java.awt.Shape getShape() {
            return circle;
        }

        /**
         * Come esta comida, lo que la elimina.
         */
        public void eat() {
            if (!eaten) {
                delete(this);
                eaten = true;
            }
        }

        /**
         * Comprueba si ya ha sido comida.
         *
         * @return <code>treue</code> si ya ha sido comida
         */
        public boolean eaten() {
            return eaten;
        }
    }

}

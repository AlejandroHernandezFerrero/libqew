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
package utils;

import java.awt.Graphics2D;

/**
 * Contabiliza el número de actualizaciones por segundo en tiempo real.
 *
 * @author Alejandro Hernández Ferrero
 */
public class FPSCounter {

    private float time;
    private int count;
    private int fps;

    /**
     * Actualiza el contador.
     *
     * @param deltaTime el tiempo que transcurrió desde la última actualización
     */
    public void update(float deltaTime) {
        count++;
        time += deltaTime;
        if (time >= 1) { // Como mínimo cada segundo
            fps = Math.round(count / time);
            time = 0;
            count = 0;
        }
    }

    /**
     * Obtiene los fps en este instante.
     *
     * @return los fps
     */
    public int getFPS() {
        return fps;
    }

    @Override
    public String toString() {
        return "" + fps;
    }

    /**
     * Dibuja este contador.
     *
     * @param g2 los gráficos en los que dibujarlo
     * @param x la posición x
     * @param y la posición y
     */
    public void paint(Graphics2D g2, int x, int y) {
        g2.drawString(fps + "", x, y);
    }

    /**
     * Dibuja este contador en la esquina superior izquierda.
     *
     * @param g2 los gráficos en los que dibujarlo
     */
    public void paint(Graphics2D g2) {
        paint(g2, 0, 0);
    }
}

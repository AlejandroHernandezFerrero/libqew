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

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;

/**
 * Funcionalidades relacionadas con la geometría.
 *
 * @author Alejandro Hernández Ferrero
 */
public class GeometryUtils {

    private static final String[] prefix = {"", "hena", "di", "tri", "tetra", "penta", "hexa", "hepta", "octa", "ennea"};

    /**
     * Obtiene el nombre de un polígono con el número de lados.
     *
     * @param sides los lados
     * @return el nombre
     */
    public static String getPolygonName(int sides) {
        if (sides > 999) {
            return sides + "-gon";
        }
        String name = "";
        int h = digit(sides, 3); // Centenas
        int t = digit(sides, 2); // Decenas
        int o = digit(sides, 1); // Unidades
        if (h > 0) { // Centenas
            if (h > 1) { // Caso base
                name += prefix[h] + "hecta";
            } else { // Excepción 100
                name += "hecto";
            }
        }
        if (t > 0) { // Decenas
            if (t > 3) { // Caso base
                name += prefix[t] + "conta";
            } else if (t == 1) { // Excepciones 10-19
                switch (o) {
                    case 0: // 10
                        break;
                    case 1: // 11
                        name += "un";
                        break;
                    case 2: // 12
                        name += "do";
                        break;
                    case 3: // 13
                        name += "tris";
                        break;
                    default: // 14-19
                        name += prefix[o];
                }
                name += "decagon";
                return name.substring(0, 1).toUpperCase() + name.substring(1); // Pone la primera letra mayúscula
            } else if (t == 2) { // Excepciones 20-29
                if (o > 0) {
                    name += "icosi"; // 21-29
                } else {
                    name += "icosa"; // 20
                }
            } else if (t == 3) { // Excepciones 30-39
                name += "triaconta";
            }
            if (o > 0) { // Si no acaba en 0 (kai=y)
                name += "kai";
            }
        }
        name += prefix[o] + "gon"; // Unidades
        return name.substring(0, 1).toUpperCase() + name.substring(1); // Pone la primera letra mayúscula
    }

    /**
     * Obtiene el dígito <code>n</code> del número.
     *
     * @param number el número
     * @param n la posición del dígito
     * @return el dígito
     */
    private static int digit(int number, int n) {
        return (number / (int) Math.pow(10, n - 1)) % 10;
    }

    /**
     * Comprueba si dos figuras intersecan.
     *
     * @param shapeA una figura
     * @param shapeB la otra figura
     * @return <code>true</code> si intersecan
     */
    public static boolean intersect(Shape shapeA, Shape shapeB) {
        Area areaA = new Area(shapeA);
        areaA.intersect(new Area(shapeB));
        return !areaA.isEmpty();
    }

    /**
     * Comprueba si la línea y el polígono intersecan.
     *
     * @param line la línea
     * @param polygon el polígono
     * @return <code>true</code> si intersecan
     */
    public static boolean intersect(Line2D line, Polygon polygon) {
        // Comprueba si la línea que forma cada lado del polígono intersecta con la línea
        for (int i = 0; i < polygon.npoints; i++) {
            if (line.intersectsLine(polygon.xpoints[i], polygon.ypoints[i], polygon.xpoints[(i + 1) % polygon.npoints], polygon.ypoints[(i + 1) % polygon.npoints])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene el área del polígono.
     *
     * @param polygon el polígono
     * @return el área
     */
    public static float getArea(Polygon polygon) {
        float sum = 0;
        for (int i = 0; i < polygon.npoints; i++) {
            sum = sum + polygon.xpoints[i] * polygon.ypoints[(i + 1) % polygon.npoints] - polygon.ypoints[i] * polygon.xpoints[(i + 1) % polygon.npoints];
        }
        return Math.abs(sum / 2);
    }

    /**
     * Obtiene el ángulo opuesto al dado.
     *
     * @param angle el ángulo
     * @return el ángulo opuesto
     */
    public static double oppositeAngle(double angle) {
        return (angle - Math.PI) % (2 * Math.PI);
    }

    /**
     * Refleja el ángulo sobre el eje x.
     *
     * @param angle el ángulo
     * @return el ángulo reflejado
     */
    public static double reflectAngleOnXAxis(double angle) {
        return -angle % (2 * Math.PI);
    }

    /**
     * Refleja el ángulo sobre el eje y.
     *
     * @param angle el ángulo
     * @return el ángulo reflejado
     */
    public static double reflectAngleOnYAxis(double angle) {
        return (Math.PI - angle) % (2 * Math.PI);
    }
}

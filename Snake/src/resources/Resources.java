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
package resources;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * Facilita el acceso a los recursos.
 *
 * @author Alejandro Hernández Ferrero
 */
public class Resources {

    private static List<Image> icons;

    /**
     * Obtiene el icono del juego en diferentes tamaños. Mantiene esta lista en
     * memoria para futuras peticiones, de forma que se evita tener que cargar
     * las imágenes cada vez.
     *
     * @return una lista con el juego en diferentes tamaños
     */
    public static List<Image> getIcons() {
        if (icons == null) {
            icons = new ArrayList<>(4);
            icons.add(new ImageIcon(Resources.class.getClassLoader().getResource("resources/snake16.png")).getImage());
            icons.add(new ImageIcon(Resources.class.getClassLoader().getResource("resources/snake32.png")).getImage());
            icons.add(new ImageIcon(Resources.class.getClassLoader().getResource("resources/snake64.png")).getImage());
            icons.add(new ImageIcon(Resources.class.getClassLoader().getResource("resources/snake128.png")).getImage());
        }
        return icons;
    }

}

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
package libqew;

import java.awt.Component;
import java.util.Collection;

/**
 * Interfaz base para todos los componentes gráficos de la librería que permiten
 * acoplar fragmentos de GUI.
 * <p>
 * Los métodos de esta interfaz se incluyen por completitud ya que el método
 * {@link java.awt.Container#add} de los componentes de esta librería ya
 * proporciona esta funcionalidad, lo que permite hacer uso de ella también
 * desde el GUI builder.
 *
 * @author Alejandro Hernández Ferrero
 */
public interface Extensible {

    /**
     * Añade una GUI hija a este componente.
     *
     * @param child la GUI hija a añadir
     */
    public void addChild(Component child);

    /**
     * Añade un conjunto de GUIs hijas a este componente.
     *
     * @param list el conjunto de GUIs hijas a añadir
     */
    public void addChildrenList(Collection<Component> list);

}

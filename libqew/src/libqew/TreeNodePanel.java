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

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.tree.MutableTreeNode;

/**
 * Clase de conveniencia para facilitar la generación de una jerarquía de nodos
 * para un {@link TreeViewPanel}.
 * <p>
 * Esta clase puede usarse como un {@link javax.swing.JPanel} normal para
 * diseñar la GUI correspondiente al nodo al que representa, pero los
 * componentes que implementen {@link MutableTreeNode} o sean instancias de esta
 * propia clase no se añadirán de forma visual sino que se añadirán como nodos
 * hijo, y solo serán accesibles cuando formen parte de un
 * {@link TreeViewPanel}. Para que sea añadido de esta forma debe ser añadido
 * directamente, es decir, si se añade un componente que contiene un nodo, ese
 * componente tratará al nodo como un componente normal y esta clase añadirá el
 * componente como un componente normal. Tampoco serán accesibles jerarquías
 * internas no conectadas directamente con la raíz.
 * <p>
 * De esta forma se proporciona un mecanismo para construir la jerarquía de
 * nodos con sus GUIs asociadas completamente desde la vista de diseño de un
 * IDE.
 *
 * @author Alejandro Hernández Ferrero
 */
public class TreeNodePanel extends ExtensiblePanel {

    private final TreeViewPanel.Node node;

    /**
     * Crea una instancia vacía.
     */
    public TreeNodePanel() {
        super();
        node = new TreeViewPanel.Node(this);
    }

    /**
     * Envuelve el componente para poder ser extendido en una jerarquía de
     * nodos.
     * <p>
     * Conveniente para crear un nodo rápidamente a partir de una GUI ya
     * diseñada, ya que por defecto este componente ocupará todo el espacio y se
     * tomará el nombre de él permanentemente.
     *
     * @param content el componente a envolver
     */
    public TreeNodePanel(Component content) {
        this();
        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
        setName(content.getName()); // Toma el nombre del componente
        content.addPropertyChangeListener("name", new PropertyChangeListener() { // Enlaza el nombre con el del componente
                                      @Override
                                      public void propertyChange(PropertyChangeEvent evt) {
                                          setName(evt.getNewValue().toString());
                                      }
                                  });
    }

    /**
     * Obtiene el nodo al que corresponde este componente.
     *
     * @return el nodo de este componente
     */
    public TreeViewPanel.Node getNode() {
        return node;
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof MutableTreeNode) { // Si implementa la interfaz
            node.add((MutableTreeNode) comp);
        } else if (comp instanceof TreeNodePanel) { // o es instancia de esta clase
            node.add(((TreeNodePanel) comp).getNode()); // se añade como nodo hijo
        } else { // Si no se añade normalmente
            super.addImpl(comp, constraints, index);
        }
    }

    /**
     * Añade el componente como nodo hijo. A diferencia de {@link #add} se añade
     * <strong>siempre</strong> como nodo hijo incluso aunque el componente no
     * lo sea, en cuyo caso se envuelve en un nodo.
     *
     * @param child el nuevo nodo hijo
     */
    @Override
    public void addChild(Component child) {
        if (child instanceof MutableTreeNode) {
            node.add((MutableTreeNode) child);
        } else if (child instanceof TreeNodePanel) {
            node.add(((TreeNodePanel) child).getNode());
        } else {
            node.add(new TreeViewPanel.Node(child));
        }
    }

}

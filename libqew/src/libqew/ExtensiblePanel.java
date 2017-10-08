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
import java.awt.Container;
import java.awt.LayoutManager;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import javax.swing.JPanel;
import libqew.ExtensibleFrame.WindowCloseListener;

/**
 * Clase base para las GUIs extensibles de la librería.
 * <p>
 * Implementa los métodos recursivos para la propagación de los eventos de
 * validar, guardar y limpiar. Ofrece dos alternativas para incrustar el código
 * personalizado a ejecutar cuando se produzcan estos eventos:<ul>
 * <li>Mediante {@link #setWindowCloseListener}.</li>
 * <li>Sobreescribiendo los métodos de {@link WindowCloseListener} que
 * implementa esta clase.</li>
 * </ul>
 * Se puede utilizar cualquiera de las dos formas o incluso las dos juntas
 * teniendo en cuenta que siempre se ejecutará antes el código insertado por el
 * segundo método.
 *
 *
 * @author Alejandro Hernández Ferrero
 */
public abstract class ExtensiblePanel extends JPanel implements Extensible {

    private WindowCloseListener listener;

    public ExtensiblePanel(LayoutManager layout) {
        super(layout);
    }

    public ExtensiblePanel() {
    }

    /**
     * Propaga los eventos de validar por la jerarquía de componentes.
     * <p>
     * Estos eventos se redirigirán tanto a las GUIs extensibles hijas como a
     * cualquier componente de la jerarquía que implemente la interfaz
     * {@link WindowCloseListener}, sin necesidad de estar directamente
     * conectados.
     *
     * @return <code>true</code> si todos los {@link WindowCloseListener} de la
     * jerarquía devuelven <code>true</code> al ser llamados a su método
     * {@link WindowCloseListener#validateThis()}
     */
    public final boolean validateAll() {
        boolean valid = true;
        // Primero se valida el propio componente
        if (this instanceof WindowCloseListener && !((WindowCloseListener) this).validateThis()) {
            valid = false;
        }
        if (listener != null && !listener.validateThis()) { // Después se valida el código insertado
            valid = false;
        }
        Queue<Component> queue = new ArrayDeque(); // Se usa una cola para recorrer la jerarquía en anchura
        for (Component comp : getComponents()) {
            queue.offer(comp);
        }
        while (!queue.isEmpty()) {
            Component component = queue.poll();
            if (component instanceof ExtensiblePanel) { // Si es una GUI extensible se delega en su propio método de propagación
                if (!((ExtensiblePanel) component).validateAll()) {
                    valid = false;
                }
            } else { // Si solo se desea que se propague por GUIs extensibles y directamente conectadas, se debe eliminar este else
                if (component instanceof WindowCloseListener) { // Si es un componente normal pero implementa la interfaz se valida
                    if (!((WindowCloseListener) component).validateThis()) {
                        valid = false;
                    }
                }
                if (component instanceof Container) { // Si es un contenedor se exploran sus hijos
                    for (Component comp : ((Container) component).getComponents()) {
                        queue.offer(comp);
                    }
                }
            }
        }
        return valid;
    }

    /**
     * Propaga los eventos de guardar por la jerarquía de componentes.
     * <p>
     * Estos eventos se redirigirán tanto a las GUIs extensibles hijas como a
     * cualquier componente de la jerarquía que implemente la interfaz
     * {@link WindowCloseListener}, sin necesidad de estar directamente
     * conectados.
     */
    public final void saveAll() {
        if (this instanceof WindowCloseListener) {
            ((WindowCloseListener) this).saveThis();
        }
        if (listener != null) {
            listener.saveThis();
        }
        Queue<Component> queue = new ArrayDeque();
        for (Component comp : getComponents()) {
            queue.offer(comp);
        }
        while (!queue.isEmpty()) {
            Component component = queue.poll();
            if (component instanceof ExtensiblePanel) {
                ((ExtensiblePanel) component).saveAll();
            } else {
                if (component instanceof WindowCloseListener) {
                    ((WindowCloseListener) component).saveThis();
                }
                if (component instanceof Container) {
                    for (Component comp : ((Container) component).getComponents()) {
                        queue.offer(comp);
                    }
                }
            }
        }
    }

    /**
     * Propaga los eventos de limpiar por la jerarquía de componentes.
     * <p>
     * Estos eventos se redirigirán tanto a las GUIs extensibles hijas como a
     * cualquier componente de la jerarquía que implemente la interfaz
     * {@link WindowCloseListener}, sin necesidad de estar directamente
     * conectados.
     */
    public final void cleanAll() {
        if (this instanceof WindowCloseListener) {
            ((WindowCloseListener) this).cleanThis();
        }
        if (listener != null) {
            listener.cleanThis();
        }
        Queue<Component> queue = new ArrayDeque();
        for (Component comp : getComponents()) {
            queue.offer(comp);
        }
        while (!queue.isEmpty()) {
            Component component = queue.poll();
            if (component instanceof ExtensiblePanel) {
                ((ExtensiblePanel) component).cleanAll();
            } else {
                if (component instanceof WindowCloseListener) {
                    ((WindowCloseListener) component).cleanThis();
                }
                if (component instanceof Container) {
                    for (Component comp : ((Container) component).getComponents()) {
                        queue.offer(comp);
                    }
                }
            }
        }
    }

    /**
     * Obtiene el {@link WindowCloseListener} asignado a esta GUI extensible.
     *
     * @return el {@link WindowCloseListener} asignado, o <code>null</code> * no
     * se asignó ninguno
     */
    public WindowCloseListener getWindowCloseListener() {
        return listener;
    }

    /**
     * Asigna un {@link WindowCloseListener} a esta GUI extensible.
     *
     * @param listener el {@link WindowCloseListener} a asignar
     */
    public void setWindowCloseListener(WindowCloseListener listener) {
        this.listener = listener;
    }

    @Override
    public void addChild(Component child) {
        add(child);
    }

    @Override
    public void addChildrenList(Collection<Component> list) {
        for (Component comp : list) {
            addChild(comp);
        }
    }

}

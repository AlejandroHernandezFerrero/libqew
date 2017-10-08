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
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * GUI extensible en pestañas.
 * <p>
 * Permite elegir el fragmento a mostrar en cada momento mediante una barra
 * superior que contiene los nombres de todos los fragmentos disponibles.
 * <p>
 * El título que se muestre para cada pestaña se tomará del nombre de su
 * contenido. Además el tamaño de la GUI se autoajustará dinámicamente según se
 * cambie de pestaña para adaptarse a su contenido; este comportamiento puede
 * desactivarse y obtenerse el por defecto de Swing mediante
 * {@link #setFixedSize}, que tomará el tamaño mínimo y preferido de la pestaña
 * más grande para todas las demás.
 *
 * @author Alejandro Hernández Ferrero
 */
public class TabsPanel extends ExtensiblePanel {

    private final JTabbedPane tabs;
    private final List<Component> components;
    private boolean fixedSize;
    private final PropertyChangeListener nameListener;

    /**
     * Crea un panel vacío al que se pueden añadir pestañas.
     */
    public TabsPanel() {
        super();
        tabs = new JTabbedPane();
        components = new ArrayList<>();
        super.setLayout(new CardLayout());
        tabs.getModel().addChangeListener(new ChangeListener() { // Avisa a la ventana para que se compacte al cambiar de pestaña
            @Override
            public void stateChanged(ChangeEvent e) {
                if (fixedSize) { // Solo si no está activado este modo
                    return;
                }
                invalidate();
                Window window = SwingUtilities.getWindowAncestor(TabsPanel.this);
                if (window != null) {
                    window.pack();
                }
            }
        });
        nameListener = new PropertyChangeListener() { // Enlaza el título de cada pestaña con el nombre de su contenido
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                int index = tabs.indexOfComponent(((Component) evt.getSource()).getParent());
                if (index > -1) {
                    tabs.setTitleAt(index, evt.getNewValue().toString());
                }
            }
        };
    }

    /**
     * Envuelve el componente para poder ser extendido en pestañas. El nombre de
     * esta GUI será tomado y enlazado con el del componente permanentemente.
     *
     * @param content el componente a envolver
     */
    public TabsPanel(Component content) {
        this();
        addChild(content);
        setName(content.getName()); // Toma el nombre del componente
        content.addPropertyChangeListener("name", new PropertyChangeListener() { // Enlaza el nombre con el del componente
                                      @Override
                                      public void propertyChange(PropertyChangeEvent evt) {
                                          setName(evt.getNewValue().toString());
                                      }
                                  });
    }

    /**
     * Este método se ha sobreescrito vacío y no hará nada, ya que esta clase
     * usa un layout concreto y no debe cambiarse.
     */
    @Override
    public final void setLayout(LayoutManager mgr) {
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (tabs.getComponentCount() == 0) { // Se añade de forma perezosa para que sea detectado correctamente por el GUI builder
            super.addImpl(tabs, null, -1);
        }
        tabs.insertTab(comp.getName(), null, new Tab(comp), null,
                       index == -1 ? tabs.getTabCount() : index);
        components.add(comp);
        comp.addPropertyChangeListener("name", nameListener);
    }

    @Override
    public Dimension getPreferredSize() {
        if (fixedSize) { // Modo por defecto de Swing
            return tabs.getPreferredSize();
        }
        Tab tab = (Tab) tabs.getSelectedComponent(); // Solo envuelve al componente y no tiene tamaño
        if (tab != null) {
            Component comp = tab.getContent(); // Es el que mantiene el tamaño real
            // El tamaño es el del componente seleccionado más la altura de la cabecera
            return new Dimension(comp.getPreferredSize().width, comp.getPreferredSize().height + tabs.getPreferredSize().height); // tabs dará el tamaño de la cabecera
        } else {
            return super.getPreferredSize();
        }
    }

    @Override
    public Dimension getMinimumSize() {
        if (fixedSize) {
            return tabs.getMinimumSize();
        }
        Tab tab = (Tab) tabs.getSelectedComponent();
        if (tab != null) {
            Component comp = tab.getContent();
            return new Dimension(comp.getMinimumSize().width, comp.getMinimumSize().height + tabs.getMinimumSize().height);
        } else {
            return super.getMinimumSize();
        }
    }

    @Override
    public Dimension getMaximumSize() {
        if (fixedSize) {
            return tabs.getMaximumSize();
        }
        Tab tab = (Tab) tabs.getSelectedComponent();
        if (tab != null) {
            Component comp = tab.getContent();
            return new Dimension(comp.getMaximumSize().width, comp.getMaximumSize().height + tabs.getMaximumSize().height);
        } else {
            return super.getMaximumSize();
        }
    }

    @Override
    public Component[] getComponents() {
        return components.toArray(new Component[0]);
    }

    /**
     * Obtiene el contenido de la pestaña n.
     *
     * @param n el índice de la pestaña
     * @return el componente en la pestaña
     */
    public Component getTab(int n) {
        return components.get(n);
    }

    @Override
    public void remove(Component comp) {
        tabs.remove(comp.getParent());
        components.remove(comp);
        comp.removePropertyChangeListener("name", nameListener);
        reset();
    }

    @Override
    public void remove(int index) {
        components.get(index).removePropertyChangeListener("name", nameListener);
        tabs.remove(index);
        components.remove(index);
        reset();
    }

    @Override
    public void removeAll() {
        for (Component comp : getComponents()) {
            comp.removePropertyChangeListener("name", nameListener);
        }
        tabs.removeAll();
    }

    /**
     * Establece el modo en el que se ajustará el tamaño de la GUI al de las
     * pestañas.
     * <ul><li><code>false</code>: el tamaño de la GUI se ajusta al de la
     * pestaña seleccionada y se autoajusta cada vez que se cambie de pestaña.
     * Es el comportamiento establecido por defecto.
     * <li><code>true</code>: el tamaño de la GUI se ajusta al de la pestaña más
     * grande y no cambia al seleccionar las demás, no permitiendo al usuario
     * redimensionar ninguna pestaña por debajo del tamaño mínimo de la más
     * grande. Es el comportamiento por defecto de Swing.</ul>
     *
     * @param fixedSize el modo en el que se ajustará el tamaño de la GUI al de
     * las pestañas
     */
    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    /**
     * Si se eliminan todas las pestañas se quita también el panel interno y se
     * deberá insertar de nuevo al añadir una pestaña para que sea detectado
     * correctamente por el GUI builder.
     */
    private void reset() {
        if (tabs.getComponentCount() == 0) {
            super.remove(0);
        }
    }

    /**
     * Se usa para implementar el modo de autoajuste de la GUI a cada pestaña.
     * Su función es ocultar la altura de todas las pestañas y la anchura de las
     * no seleccionadas, de forma que cuando se calcule el tamaño del
     * {@link javax.swing.JTabbedPane} que las contiene se obtenga el tamaño de
     * la cabecera, que es necesario para, junto al tamaño del componente
     * seleccionado, calcular el tamaño total de la GUI en ese momento. Es
     * necesario obtener el tamaño de forma dinámica ya que dependiendo de la
     * anchura de cada componente las pestañas de la cabecera pueden ocupar
     * distinto número de filas y por tanto el tamaño de la cabecera cambiaría.
     */
    private class Tab extends JPanel {

        private final Component content;

        /**
         * Solo envuelve al componente
         */
        public Tab(Component content) {
            this.content = content;
            setLayout(new BorderLayout());
            add(content, BorderLayout.CENTER);
        }

        public Component getContent() {
            return content;
        }

        @Override
        public Dimension getPreferredSize() {
            if (fixedSize) { // Si está activado este modo se anula el comportamiento de esta clase
                return content.getPreferredSize();
            }
            if (equals(tabs.getSelectedComponent())) { // Si es la pestaña seleccionada da su anchura y 0 de altura
                return new Dimension(content.getPreferredSize().width, 0);
            } else { // Si no da tamaño 0
                return new Dimension(0, 0);
            }
        }

        @Override
        public Dimension getMinimumSize() {
            if (fixedSize) {
                return content.getMinimumSize();
            }
            if (equals(tabs.getSelectedComponent())) {
                return new Dimension(content.getMinimumSize().width, 0);
            } else {
                return new Dimension(0, 0);
            }
        }

        @Override
        public Dimension getMaximumSize() {
            if (fixedSize) {
                return content.getMaximumSize();
            }
            if (equals(tabs.getSelectedComponent())) {
                return new Dimension(content.getMaximumSize().width, 0);
            } else {
                return new Dimension(0, 0);
            }
        }

    }
}

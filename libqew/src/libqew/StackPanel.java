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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * GUI extensible en una sola página.
 * <p>
 * Permite apilar fragmentos de GUI de forma vertical y distribuye el espacio de
 * forma proporcional al tamaño de cada fragmento.
 *
 * @author Alejandro Hernández Ferrero
 */
public class StackPanel extends ExtensiblePanel {

    private Component content;
    private GridBagLayout layout;
    private boolean nameSet;
    private final PropertyChangeListener nameListener;

    /**
     * Crea una GUI extensible en una sola página vacía que se puede rellenar
     * posteriormente con fragmentos.
     */
    public StackPanel() {
        super();
        layout = new GridBagLayout();
        super.setLayout(layout);
        nameListener = new PropertyChangeListener() { // Toma el nombre de su contenido
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!nameSet) {
                    StackPanel.super.setName(evt.getNewValue().toString());
                }
            }
        };
    }

    /**
     * Crea una GUI extensible que permitirá apilar fragmentos debajo del
     * componente.
     * <p>
     * Llamadas sucesivas a {@link #setContent} sustituirán este componente.
     *
     * @param content el componente a envolver.
     */
    public StackPanel(Component content) {
        this();
        setContent(content);
    }

    /**
     * Este método se ha sobreescrito vacío y no hará nada, ya que esta clase
     * usa un layout concreto y no debe cambiarse.
     */
    @Override
    public final void setLayout(LayoutManager mgr) {
    }

    @Override
    public void setName(String name) {
        if (content != null) {
            this.content.removePropertyChangeListener("name", nameListener);
        }
        nameSet = true;
        super.setName(name);
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index);
        invalidate();
    }

    @Override
    public void invalidate() {
        /*
         * Cada vez que cambia un componente hay que recalcular los pesos de
         * cada fragmento para que se distribuya el tamaño proporcionalmente
         */
        for (int i = 0; i < getComponentCount(); i++) {
            Component comp = getComponent(i);
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = i;
            c.gridx = 0;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            c.weighty = comp.getPreferredSize().height;
            layout.setConstraints(comp, c);
        }
        super.invalidate();
    }

    /**
     * Obtiene el componente establecido como base de esta GUI extensible.
     *
     * @return el componente establecido como base o <code>null</code> si no se
     * ha establecido
     */
    public Component getContent() {
        return content;
    }

    /**
     * Establece un componente como base de esta GUI extensible, que será
     * mostrado siempre en la parte superior de la GUI. Si ya había otro
     * componente establecido como tal se reemplaza. Si no se ha establecido un
     * nombre explícitamente se tomará el de este componente.
     *
     * @param content el componente base
     */
    public void setContent(Component content) {
        if (this.content != null) { // Si ya estaba establecido se elimina el antiguo
            remove(this.content);
            if (!nameSet) {
                this.content.removePropertyChangeListener("name", nameListener);
            }
        }
        this.content = content;
        if (content != null) {
            add(content, 0); // Se añade en la posición superior
            if (!nameSet) {
                super.setName(content.getName());
                content.addPropertyChangeListener("name", nameListener); // Si no se ha establecido un nombre explícitamente se toma el de este componente
            }
        }
    }

}

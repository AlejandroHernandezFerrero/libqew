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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Ventana que envuelve con botones de aceptar y cancelar cualquier tipo de GUI
 * y se encarga de propagar los eventos que desencadenan estos botones a todos
 * los componentes que contiene. También permite acoplar fragmentos de GUI, que
 * serán incrustados en la GUI extensible establecida como contenido o en una
 * sola página si no se estableció ninguna.
 *
 * @author Alejandro Hernández Ferrero
 */
public class ExtensibleFrame extends Frame implements Extensible {

    private ExtensiblePanel content;
    private JPanel buttonsPanel;
    private WindowCloseListener listener;

    /**
     * Crea una ventana vacía que solo contendrá los botones. Se puede añadir
     * posteriormente una GUI extensible a la que envolver mediante
     * {@link #setContent}, o añadir GUIs hijas directamente, en cuyo caso se
     * usará como padre un <code>StackPanel</code>.
     */
    public ExtensibleFrame() {
        super();
        initComponents();
    }

    /**
     * Crea una ventana que envuelve con botones al componente. Si el componente
     * no es una GUI extensible, se envuelve antes con un
     * <code>StackPanel</code> para que la ventana pueda seguir siendo
     * extensible.
     *
     * @param content la GUI a envolver
     */
    public ExtensibleFrame(Component content) {
        super();
        initComponents();
        if (content instanceof ExtensiblePanel) {
            setContent((ExtensiblePanel) content);
        } else {
            StackPanel panel = new StackPanel((Component) content);
            setContent(panel);
        }
    }

    /**
     * Crea una ventana que usa el tipo de GUI extensible especificado al
     * añadirle hijos.
     *
     * @param type el tipo de GUI extensible a emplear
     */
    public ExtensibleFrame(Class<? extends ExtensiblePanel> type) {
        super();
        initComponents();
        try {
            setContent(type.newInstance());
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ExtensibleFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComponents() {
        setOversizedPolicy(true, true, false); // Muestra error si es más grande que la pantalla, se puede cambiar si se desea otro comportamiento
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout());
        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ok();
            }
        });
        buttons.add(ok);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtensibleFrame.this.dispatchEvent(new WindowEvent(ExtensibleFrame.this, WindowEvent.WINDOW_CLOSING));
            }
        });
        buttons.add(cancel);
        buttonsPanel = new JPanel();
        buttonsPanel.add(buttons);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); // La aplicación no debe cerrarse cuando se cierre un diálogo
        pack();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancel(); // Se llama al cerrar y no al pulsar el botón para que se limpie también cuando se cierre sin pulsar el botón
            }
        });
    }

    private void ok() {
        // Primero se valida el propio componente
        if (this instanceof WindowCloseListener && !((WindowCloseListener) this).validateThis()) {
            return; // Si se valida a falso no hace falta seguir
        }
        if (getContent().validateAll() && (listener == null || listener.validateThis())) {
            if (this instanceof WindowCloseListener) {
                ((WindowCloseListener) this).saveThis();
            }
            getContent().saveAll();
            if (listener != null) {
                listener.saveThis();
            }
            ExtensibleFrame.this.dispatchEvent(new WindowEvent(ExtensibleFrame.this, WindowEvent.WINDOW_CLOSING)); // Antes de cerrarse se llamará a cancel y ahí se limpiará
        }
    }

    private void cancel() {
        if (this instanceof WindowCloseListener) {
            ((WindowCloseListener) this).cleanThis();
        }
        getContent().cleanAll();
        if (listener != null) {
            listener.cleanThis();
        }
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (content != null) { // Si tiene una GUI extensible interna se añade a ella
            content.add(comp, constraints, index);
            pack();
        } else { // Si no, se añade normal
            super.addImpl(comp, constraints, index);
        }
    }

    @Override
    public void addChild(Component child) {
        getContent().addChild(child);
        pack();
    }

    @Override
    public void addChildrenList(Collection<Component> list) {
        getContent().addChildrenList(list);
        pack();
    }

    /**
     * Establece la GUI extensible a la que envuelve esta ventana. Si ya había
     * una anteriormente se elimina.
     *
     * @param content la GUI extensible a envolver
     */
    public void setContent(ExtensiblePanel content) {
        if (content != null) {
            this.content = content;
            setContentPane(new JPanel(new BorderLayout()));
            if (content instanceof TreeNodePanel) { // Si es un TreeNodePanel se envuelve en vista de árbol para visualizarlo
                this.content = new TreeViewPanel();
                this.content.addChild(content);
            }
            getContentPane().add(this.content, BorderLayout.CENTER);
            getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
            if (getTitle().equals("")) { // Si no se le ha puesto título a la ventana hereda el de la GUI
                setTitle(this.content.getName());
            }
            pack();
        }
    }

    /**
     * Obtiene la GUI extensible envuelta por esta ventana. Si no se ha
     * establecido se crea una de tipo en una sola página.
     *
     * @return la GUI extensible envuelta por esta ventana
     */
    public ExtensiblePanel getContent() {
        if (content == null) {
            setContent(new StackPanel());
        }
        return content;
    }

    /**
     * Obtiene el <code>WindowCloseListener</code> establecido en esta ventana.
     *
     * @return el <code>WindowCloseListener</code> establecido en esta ventana
     */
    public WindowCloseListener getWindowCloseListener() {
        return listener;
    }

    /**
     * Establece un <code>WindowCloseListener</code> para esta ventana. Este
     * listener será llamado, si procede, siempre en último lugar, después de
     * llamar recursivamente a todos los de las GUIs internas.
     *
     * @param listener el listener
     */
    public void setWindowCloseListener(WindowCloseListener listener) {
        this.listener = listener;
    }

    /**
     * Crea y muestra una ventana envolviendo al componente.
     *
     * @param content el componente a envolver
     * @return la ventana creada
     */
    public static ExtensibleFrame showWindow(Component content) {
        ExtensibleFrame frame = createWindow(content);
        frame.setVisible(true);
        return frame;
    }

    /**
     * Crea y muestra una ventana envolviendo al componente.
     *
     * @param content el componente a envolver
     * @param icons la lista de iconos a ser usada como iconos de la ventana
     * @return la ventana creada
     */
    public static ExtensibleFrame showWindow(Component content, List<Image> icons) {
        ExtensibleFrame frame = createWindow(content, icons);
        frame.setVisible(true);
        return frame;
    }

    /**
     * Crea una ventana envolviendo al componente.
     *
     * @param content el componente a envolver
     * @return la ventana creada
     */
    public static ExtensibleFrame createWindow(Component content) {
        ExtensibleFrame frame = new ExtensibleFrame(content);
        frame.setTitle(content.getName());
        return frame;
    }

    /**
     * Crea una ventana envolviendo al componente.
     *
     * @param content el componente a envolver
     * @param icons la lista de iconos a ser usada como iconos de la ventana
     * @return la ventana creada
     */
    public static ExtensibleFrame createWindow(Component content, List<Image> icons) {
        ExtensibleFrame frame = createWindow(content);
        frame.setIconImages(icons);
        return frame;
    }

    /**
     * Encapsula el código personalizado que se ejecutará cuando se produzcan
     * los eventos de validar, guardar y limpiar al cerrar la ventana.
     * <p>
     * Según se pulse el botón de aceptar o cancelar (o cerrar la ventana), se
     * desencadenará una secuencia distinta de eventos:<ul>
     * <li><strong> Aceptar</strong>: si <code>validar</code> entonces
     * <code>guardar</code> y <code>limpiar</code>.</li>
     * <li><strong> Cancelar</strong>: <code>limpiar</code>.</li>
     * </ul>
     * <p>
     *
     * @author Alejandro Hernández Ferrero
     */
    public interface WindowCloseListener {

        /**
         * Valida los datos de esta clase. Si no son válidos no se seguirá con
         * el proceso de guardar.
         *
         * @return <code>true</code> si los datos son válidos
         */
        public boolean validateThis();

        /**
         * Guarda los datos de esta clase.
         */
        public void saveThis();

        /**
         * Limpia los datos de esta clase.
         */
        public void cleanThis();

    }

}

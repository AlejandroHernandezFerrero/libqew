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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 * Ventana que proporciona mayor soporte en cuanto al redimensionamiento. Se
 * encarga de ajustar, además del tamaño preferido como una ventana normal, el
 * tamaño mínimo y máximo según su contenido y teniendo en cuenta los bordes.
 * Además detecta si la ventana no entra en la pantalla y ofrece formas
 * configurables de actuar al respecto.
 *
 * @author Alejandro Hernández Ferrero
 */
public class Frame extends JFrame {

    private boolean pack;
    private boolean hasScrollBars;

    private boolean addScrollBar = true;
    private boolean closeWindow = false;
    private boolean showWarning = false;

    /**
     * Crea una nueva ventana que estará inicialmente invisible.
     */
    public Frame() {
        super();
        setLocationByPlatform(true); // Para que el SO se encargue de la posición inicial
    }

    /**
     * Ajusta el tamaño preferido, mínimo y máximo de esta ventana al de su
     * contenido.
     */
    @Override
    public void pack() {
        if (!isVisible()) { // Si la ventana está oculta aplazamos hacerlo hasta que deje de estarlo
            pack = true;
            return;
        }
        Dimension border = new Dimension(); // Tamaño del borde de la ventana
        border.setSize(getInsets().left + getInsets().right, getInsets().top + getInsets().bottom);
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds(); // Tamaño de la pantalla
        Dimension pref = new Dimension();
        pref.setSize(getContentPane().getPreferredSize().getWidth() + border.getWidth(), getContentPane().getPreferredSize().getHeight() + border.getHeight());
        pref.setSize(Math.min(pref.getWidth(), screen.getWidth()), Math.min(pref.getHeight(), screen.getHeight())); // El tamaño será como mucho el de la pantalla
        setPreferredSize(pref);
        Dimension max = new Dimension();
        max.setSize(getContentPane().getMaximumSize().getWidth() + border.getWidth(), getContentPane().getMaximumSize().getHeight() + border.getHeight());
        max.setSize(Math.min(max.getWidth(), screen.getWidth()), Math.min(max.getHeight(), screen.getHeight())); // El tamaño será como mucho el de la pantalla
        setMaximumSize(max);
        Dimension min = new Dimension();
        min.setSize(getContentPane().getMinimumSize().getWidth() + border.getWidth(), getContentPane().getMinimumSize().getHeight() + border.getHeight());
        boolean oversized = false;
        // Si el tamaño mínimo de la ventana es mayor que la pantalla lo solucionamos aplicando la política indicada
        if (min.getWidth() > screen.getWidth() || min.getHeight() > screen.getHeight()) {
            oversized = true;
            if (addScrollBar) { // Se añaden barras de scroll que permitirán visualizar toda la ventana
                JScrollPane scrollPane = new JScrollPane(getContentPane());
                // Se comprueba qué barras hacen falta
                boolean horizontalBar = min.getWidth() > screen.getWidth();
                boolean verticalBar = min.getHeight() > screen.getHeight();
                // Se comprueba si el espacio que se pierde al añadir una barra hace necesaria la otra
                if (horizontalBar && !verticalBar) {
                    verticalBar = min.getHeight() + scrollPane.getHorizontalScrollBar().getMaximumSize().height > screen.getHeight();
                }
                if (!horizontalBar && verticalBar) {
                    horizontalBar = min.getWidth() + scrollPane.getVerticalScrollBar().getMaximumSize().width > screen.getWidth();
                }
                scrollPane.setHorizontalScrollBarPolicy(horizontalBar ? JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS : JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.setVerticalScrollBarPolicy(verticalBar ? JScrollPane.VERTICAL_SCROLLBAR_ALWAYS : JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                super.setContentPane(scrollPane);
                hasScrollBars = true;
                // Se tiene en cuenta el espacio extra para las barras de scroll
                int scrollBarWidth = verticalBar ? scrollPane.getVerticalScrollBar().getMaximumSize().width : 0;
                int scrollBarHeight = horizontalBar ? scrollPane.getHorizontalScrollBar().getMaximumSize().height : 0;
                min.setSize(Math.min(min.getWidth() + scrollBarWidth, screen.getWidth()), Math.min(min.getHeight() + scrollBarHeight, screen.getHeight()));

            }
        } else if (hasScrollBars) { // Si ahora ya entra se eliminan las barras de scroll
            super.setContentPane(getContentPane());
            hasScrollBars = false;
        }
        setMinimumSize(min);
        // Si se sale de la pantalla se reposiciona
        if (getLocation().x + pref.width > screen.getWidth()) {
            setLocation(Math.max(0, screen.width - pref.width), getLocation().y);
        }
        if (getLocation().y + pref.height > screen.getHeight()) {
            setLocation(getLocation().x, Math.max(0, screen.height - pref.height));
        }
        pack = false;
        super.pack();
        if (oversized) {
            if (showWarning) {
                JOptionPane.showMessageDialog(this, "The frame is too big for this screen.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (closeWindow) {
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
        }
    }

    /**
     * Permite realizar peticiones de {@link #pack()} de forma más eficiente. Es
     * especialmente útil cuando se realizan múltiples peticiones simultáneas,
     * ya que solo se efectuará {@link #pack()} una única vez en un instante
     * posterior.
     */
    public void repack() {
        pack = true;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        if (pack) {
            pack();
        }
        super.paint(g);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b && pack) {
            pack();
        }
    }

    @Override
    public Container getContentPane() {
        if (hasScrollBars) {
            return (Container) ((JScrollPane) super.getContentPane()).getViewport().getView();
        }
        return super.getContentPane();
    }

    @Override
    public void setContentPane(Container contentPane) {
        if (hasScrollBars) {
            ((JScrollPane) super.getContentPane()).setViewportView(contentPane);
        }
        super.setContentPane(contentPane);
    }

    /**
     * Establece la política que se seguirá cuando la ventana no entre en la
     * pantalla.
     *
     * @param addScrollBar añadir barras de scroll para poder visualizarla
     * entera
     * @param showWarning mostrar un mensaje de advertencia
     * @param closeWindow cerrar la ventana
     */
    public void setOversizedPolicy(boolean addScrollBar, boolean showWarning, boolean closeWindow) {
        this.addScrollBar = addScrollBar;
        this.showWarning = showWarning;
        this.closeWindow = closeWindow;
    }

    /**
     * Establece si se añadirán barras de scroll cuando la ventana no entre en
     * la pantalla.
     *
     * @param addScrollBar si se añadirán barras de scroll
     */
    public void setAddScrollBarPolicy(boolean addScrollBar) {
        this.addScrollBar = addScrollBar;
    }

    /**
     * Establece si se cerrará la ventana cuando no entre en la pantalla.
     *
     * @param closeWindow si se cerrará la ventana
     */
    public void setCloseWindowPolicy(boolean closeWindow) {
        this.closeWindow = closeWindow;
    }

    /**
     * Establece si se mostrará una advertencia cuando la ventana no entre en la
     * pantalla.
     *
     * @param showWarning si se mostrará un mensaje de advertencia
     */
    public void setShowWarningPolicy(boolean showWarning) {
        this.showWarning = showWarning;
    }

}

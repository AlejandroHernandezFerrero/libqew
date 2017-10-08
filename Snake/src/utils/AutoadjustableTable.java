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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Tabla que se autoajusta cuando su contenido cambia.
 *
 * @author Alejandro Hernández Ferrero
 */
public class AutoadjustableTable extends JTable {

    /**
     * Crea una nueva tabla autoajustable.
     *
     * @param model el modelo de datos
     */
    public AutoadjustableTable(TableModel model) {
        super(model);
        adjustColumns();
        setFillsViewportHeight(true);
        setPreferredScrollableViewportSize(getPreferredSize());
        getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                Dimension oldSize = getPreferredSize();
                int column = convertColumnIndexToView(e.getColumn());
                if (e.getType() == TableModelEvent.UPDATE && column != -1) { // Si solo se modifica 1 celda
                    adjustColumn(column); // Solo se recalcula el tamaño de la columna
                } else { // Si no se recalcula toda la tabla
                    adjustColumns();
                }
                Dimension newSize = getPreferredSize();
                setPreferredScrollableViewportSize(newSize);
                if (!newSize.equals(oldSize)) { // Si cambió el tamaño avisa a la ventana para que se reajuste
                    Window window = (Window) SwingUtilities.getWindowAncestor(AutoadjustableTable.this);
                    if (window != null) {
                        window.pack();
                    }
                }
            }
        });
    }

    /**
     * Ajusta el tamaño de cada columna al mínimo en el que entren todos los
     * datos.
     */
    public void adjustColumns() {
        TableColumnModel tcm = getColumnModel();
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            adjustColumn(i);
        }
    }

    /**
     * Ajusta el tamaño de la columna al mínimo en el que entren todos los
     * datos.
     *
     * @param column el índice de la columna
     */
    public void adjustColumn(int column) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        if (!tableColumn.getResizable()) {
            return;
        }
        int columnHeaderWidth = getColumnHeaderWidth(column);
        int columnDataWidth = getColumnDataWidth(column);
        int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);
        updateTableColumn(column, preferredWidth);
    }

    /**
     * Calcula la anchura necesaria para mostrar el título de la columna.
     */
    private int getColumnHeaderWidth(int column) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        Object value = tableColumn.getHeaderValue();
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();
        if (renderer == null) {
            renderer = getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(this, value, false, false, -1, column);
        return comp.getPreferredSize().width;
    }

    /**
     * Calcula la anchura necesaria para mostrar los datos de la columna.
     */
    private int getColumnDataWidth(int column) {
        int preferredWidth = 0;
        int maxWidth = getColumnModel().getColumn(column).getMaxWidth();
        // Recorre todas las celdas para comprobar cuál es la más ancha y devuelve su anchura
        for (int row = 0; row < getRowCount(); row++) {
            preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column));
            if (preferredWidth >= maxWidth) { // No puede ser más grande que este valor así que no hace falta seguir
                break;
            }
        }
        return preferredWidth;
    }

    /**
     * Calcula la anchura necesaria para mostrar los datos de una celda.
     */
    private int getCellDataWidth(int row, int column) {
        TableCellRenderer cellRenderer = getCellRenderer(row, column);
        Component c = prepareRenderer(cellRenderer, row, column);
        int width = c.getPreferredSize().width + getIntercellSpacing().width;
        return width;
    }

    /**
     * Establece la anchura de la columna.
     */
    private void updateTableColumn(int column, int width) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        if (!tableColumn.getResizable()) {
            return;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setWidth(width);
    }

}

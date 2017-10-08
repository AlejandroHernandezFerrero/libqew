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
package ui;

import game.Enemies;
import game.EnemyType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import libqew.ExtensibleFrame.WindowCloseListener;
import libqew.ExtensiblePanel;
import libqew.TreeNodePanel;
import libqew.TreeViewPanel.Node;
import utils.AutoadjustableTable;
import utils.ColorUtils.ColorName;
import utils.ColorUtils.ColorRenderer;
import utils.ColorUtils.ColorEditor;
import utils.Mailbox;
import utils.Messenger;
import utils.RandomUtils.Random;
import utils.RandomUtils.RandomBoolean;
import utils.RandomUtils.RandomGaussian;
import utils.Synchronizer;

/**
 * GUI que permite editar los atributos de {@link EnemyType}.
 *
 * @author Alejandro Hernández Ferrero
 */
public class EnemiesUI extends TreeNodePanel implements WindowCloseListener {

    private static final String[] columnKeys = {"fill", "fillColor", "border", "borderColor", "rotate", "clockwise", "angularSpeed", "turningSpeed", "initialAngle", "speed", "radius", "sides", "copies"};

    private ArrayList<EnemyType> list;
    private final Enemies enemies;
    private final Enemies.Generator generator;
    private final AutoadjustableTable tableEnemies;
    private final AutoadjustableTable tableGenerator;

    /**
     * Crea una nueva GUI que modifica el grupo de enemigos.
     *
     * @param enemies el grupo de enemigos
     */
    public EnemiesUI(Enemies enemies) {
        super();
        setLayout(new GridBagLayout());
        initComponents();
        this.enemies = enemies;
        setName("Enemies");
        linkList();
        generator = enemies.getGenerator().copy();
        tableEnemies = new AutoadjustableTable(new TableEnemiesModel());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(tableEnemies);
        scrollPane.setMinimumSize(new Dimension(tableEnemies.getWidth(), 80));
        panelEnemies.add(scrollPane, BorderLayout.CENTER);
        tableEnemies.setDefaultRenderer(java.awt.Color.class, new ColorRenderer());
        tableEnemies.setDefaultEditor(java.awt.Color.class, new ColorEditor());
        tableEnemies.adjustColumns();
        panelEnemies.add(buttons, BorderLayout.SOUTH);
        tableGenerator = new AutoadjustableTable(new TableGeneratorModel());
        panelGenerator.add(tableGenerator.getTableHeader(), BorderLayout.NORTH);
        panelGenerator.add(tableGenerator, BorderLayout.CENTER);
        panelGenerator.add(buttonGenerate, BorderLayout.SOUTH);
        ((TableEnemiesModel) tableEnemies.getModel()).fireTableRowsInserted(0, list.size() - 1);
        total.setText("Total: " + list.size() + " enemies.");
        linkActions();
    }

    @Override
    public boolean validateThis() {
        return true;
    }

    @Override
    public void cleanThis() {
        int n = (int) Mailbox.receiveMessage("ENEMIESUI_OPEN"); // Número de instancias abiertas
        n--;
        if (n < 1) { // Si no hay más se borran los mensajes
            Mailbox.removeMessage("ENEMIES_LIST");
            Mailbox.removeMessage("ENEMIESUI_OPEN");
        } else {
            Mailbox.sendMessage("ENEMIESUI_OPEN", n);
        }
        Synchronizer.unregister(this);
    }

    @Override
    public void saveThis() {
        enemies.setEnemies(list);
        enemies.setGenerator(generator);
    }

    /**
     * Enlaza la lista de enemigos con las demás GUIs abiertas.
     */
    private void linkList() {
        list = (ArrayList<EnemyType>) Mailbox.receiveMessage("ENEMIES_LIST"); // Pide la lista compartida
        if (list == null) { // Si no existe la crea esta instancia
            list = new ArrayList<>();
            for (EnemyType enemy : enemies.getEnemies()) {
                list.add(new EnemyType(enemy));
            }
            Mailbox.sendMessage("ENEMIES_LIST", list); // La comparte
            Mailbox.sendMessage("ENEMIESUI_OPEN", 1);
        } else { // Contabiliza el número de instancias abiertas
            int n = (int) Mailbox.receiveMessage("ENEMIESUI_OPEN");
            Mailbox.sendMessage("ENEMIESUI_OPEN", ++n);
        }
        for (EnemyType enemy : list) {
            addEnemyUI(enemy);
        }
    }

    /**
     * Enlaza eventos producidos en un botón con acciones en todas las GUIs.
     */
    private void linkActions() {
        new Synchronizer.Listener("ENEMIESUI_ADD", this) { // Añadir
            @Override
            public void receive(Object key, Object message) {
                ((TableEnemiesModel) tableEnemies.getModel()).fireTableRowAdded();
                total.setText("Total: " + list.size() + " enemies.");
                addEnemyUI((EnemyType) message);
            }
        };
        new Synchronizer.Listener("ENEMIESUI_DELETE", this) { // Borrar
            @Override
            public void receive(Object key, Object message) {
                if (tableEnemies.getCellEditor() != null) { // Si estaba editando esa fila se deja de editar
                    tableEnemies.getCellEditor().stopCellEditing();
                }
                tableEnemies.clearSelection(); // Se elimina la selección ya que serán siempre los eliminados
                int index = (int) message;
                ((TableEnemiesModel) tableEnemies.getModel()).fireTableRowDeleted(index);
                // Hay que avisar a la GUI borrada para que libere sus recursos
                ((ExtensiblePanel) ((Node) getNode().getChildAt(index)).getUserObject()).cleanAll();
                getNode().removeChild(index);
                total.setText("Total: " + list.size() + " enemies.");
            }
        };
        new Synchronizer.Listener("ENEMIESUI_GENERATE", this) { // Generar
            @Override
            public void receive(Object key, Object message) {
                int startRow = (int) message;
                ((TableEnemiesModel) tableEnemies.getModel()).fireTableRowsInserted(startRow, list.size() - 1);
                total.setText("Total: " + list.size() + " enemies.");
                for (int i = startRow; i < list.size(); i++) {
                    addEnemyUI(list.get(i));
                }
            }
        };
    }

    /**
     * Enlaza los datos de un enemigo en la tabla con su GUI.
     */
    private void linkEnemy(EnemyType enemy) {
        int column = 0;
        for (String key : columnKeys) {
            key = enemy.id() + key; // Identifica un dato de un enemigo concreto
            int c = column;
            column++;
            new Synchronizer.Listener(key, enemy) {
                @Override
                public void receive(Object key, Object message) {
                    int row = list.indexOf(enemy);
                    // Hay que comprobar que el valor no sea el mismo para evitar un bucle de avisos a sí mismo
                    if (!tableEnemies.getModel().getValueAt(row, c).equals(message)) {
                        tableEnemies.getModel().setValueAt(message, row, c);
                    }
                    ((TableEnemiesModel) tableEnemies.getModel()).fireTableCellUpdated(row, c);
                }
            };
        }
    }

    /**
     * Añade la GUI de un enemigo como hija de ésta.
     */
    private void addEnemyUI(EnemyType enemy) {
        ExtensiblePanel ui = enemy.getUI();
        ui.setName(enemy.toString());
        addChild(ui);
        linkEnemy(enemy);
        new Synchronizer.Listener(enemy.id() + columnKeys[1], enemy) { // Enlaza el color con el nombre de la GUI
            @Override
            public void receive(Object key, Object message) {
                ui.setName(enemy.toString());
            }
        };
        new Synchronizer.Listener(enemy.id() + columnKeys[11], enemy) { // Enlaza los lados con el nombre de la GUI
            @Override
            public void receive(Object key, Object message) {
                ui.setName(enemy.toString());
            }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGenerate = new javax.swing.JButton();
        buttons = new javax.swing.JPanel();
        total = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        clear = new javax.swing.JButton();
        delete = new javax.swing.JButton();
        add = new javax.swing.JButton();
        panelEnemies = new javax.swing.JPanel();
        panelGenerator = new javax.swing.JPanel();

        buttonGenerate.setText("Generate");
        buttonGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGenerateActionPerformed(evt);
            }
        });

        buttons.setLayout(new java.awt.GridBagLayout());

        total.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 15);
        buttons.add(total, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        clear.setText("Clear");
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });
        jPanel1.add(clear);

        delete.setText("Delete");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });
        jPanel1.add(delete);

        add.setText("Add");
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });
        jPanel1.add(add);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        buttons.add(jPanel1, gridBagConstraints);

        setLayout(new java.awt.GridBagLayout());

        panelEnemies.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        add(panelEnemies, gridBagConstraints);

        panelGenerator.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Random generator", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP));
        panelGenerator.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weightx = 0.1;
        add(panelGenerator, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void buttonGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGenerateActionPerformed
        ArrayList<EnemyType> generated = new ArrayList<>();
        generator.generate(generated);
        int startRow = list.size();
        list.addAll(generated);
        Messenger.sendMessage("ENEMIESUI_GENERATE", startRow); // Envía un mensaje informando del evento a todas las GUIs
    }//GEN-LAST:event_buttonGenerateActionPerformed

    private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
        if (list.isEmpty()) { // Si ya está vacía no se hace nada
            return;
        }
        int last = list.size() - 1;
        for (int i = last; i >= 0; i--) {
            EnemyType enemy = list.remove(i);
            Messenger.sendMessage("ENEMIESUI_DELETE", i); // Envía un mensaje informando del evento a todas las GUIs
            Synchronizer.unregister(enemy); // Elimina los listeners que se crearon asociados a él
        }
    }//GEN-LAST:event_clearActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        int[] rows = tableEnemies.getSelectedRows();
        for (int i = rows.length - 1; i >= 0; i--) {
            int index = rows[i];
            EnemyType enemy = list.remove(index);
            Messenger.sendMessage("ENEMIESUI_DELETE", index);
            Synchronizer.unregister(enemy);
        }
    }//GEN-LAST:event_deleteActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        EnemyType enemy = generator.createRandomEnemy();
        list.add(enemy);
        Messenger.sendMessage("ENEMIESUI_ADD", enemy); // Envía un mensaje informando del evento a todas las GUIs
    }//GEN-LAST:event_addActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JButton buttonGenerate;
    private javax.swing.JPanel buttons;
    private javax.swing.JButton clear;
    private javax.swing.JButton delete;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panelEnemies;
    private javax.swing.JPanel panelGenerator;
    private javax.swing.JLabel total;
    // End of variables declaration//GEN-END:variables

    /**
     * Modelo de datos de la tabla del generador.
     */
    public class TableGeneratorModel extends AbstractTableModel {

        private final String[] columnNames = {"Property", "Mean/probability", "Std dev", "Min", "Max"};
        private final String[] rowNames = {"Filled", "Border", "Rotate", "Clockwise", "Hue", "Fill saturation", "Fill luminance", "Border saturation", "Border luminance", "Sides", "Radius", "Speed per area", "Turning speed", "Angular speed", "Copies", "Count"};

        @Override
        public int getRowCount() {
            return rowNames.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? String.class : Float.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            Object property = getProperty(rowIndex);
            if (property instanceof RandomGaussian) {
                return columnIndex > 0 && columnIndex < 6;
            }
            if (property instanceof Random) {
                return columnIndex > 2 && columnIndex < 6;
            }
            if (property instanceof RandomBoolean) {
                return columnIndex > 0 && columnIndex < 2;
            }
            return false;
        }

        private Object getProperty(int row) {
            switch (row) {
                case 0:
                    return generator.getFill();
                case 1:
                    return generator.getBorder();
                case 2:
                    return generator.getRotate();
                case 3:
                    return generator.getClockwise();
                case 4:
                    return generator.getHue();
                case 5:
                    return generator.getFillSaturation();
                case 6:
                    return generator.getFillLuminance();
                case 7:
                    return generator.getBorderSaturation();
                case 8:
                    return generator.getBorderLuminance();
                case 9:
                    return generator.getSides();
                case 10:
                    return generator.getRadius();
                case 11:
                    return generator.getSpeed();
                case 12:
                    return generator.getTurningSpeed();
                case 13:
                    return generator.getAngularSpeed();
                case 14:
                    return generator.getCopies();
                case 15:
                    return generator.getCount();
                default:
                    return null;
            }
        }

        private boolean valid(float value, int row) {
            switch (row) {
                case 0:
                    return value >= 0 && value <= 1;
                case 1:
                    return value >= 0 && value <= 1;
                case 2:
                    return value >= 0 && value <= 1;
                case 3:
                    return value >= 0 && value <= 1;
                case 4:
                    return value >= 0 && value <= 1;
                case 5:
                    return value >= 0 && value <= 1;
                case 6:
                    return value >= 0 && value <= 1;
                case 7:
                    return value >= 0 && value <= 1;
                case 8:
                    return value >= 0 && value <= 1;
                case 9:
                    return value >= 3 && value <= 9999;
                case 10:
                    return value >= 1 && value <= 9999;
                case 11:
                    return value >= 0 && value <= 9999;
                case 12:
                    return value >= 0 && value <= 9999;
                case 13:
                    return value >= 0 && value <= 9999;
                case 14:
                    return value >= 0 && value <= 9999;
                case 15:
                    return value >= 0 && value <= 9999;
                default:
                    return false;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return rowNames[rowIndex];
            }
            Object property = getProperty(rowIndex);
            if (property instanceof RandomGaussian) {
                RandomGaussian value = (RandomGaussian) property;
                switch (columnIndex) {
                    case 1:
                        return value.getMean();
                    case 2:
                        return value.getStdDev();
                    case 3:
                        return value.getMin();
                    case 4:
                        return value.getMax();
                    default:
                        return null;
                }
            }
            if (property instanceof Random) {
                Random value = (Random) property;
                switch (columnIndex) {
                    case 3:
                        return value.getMin();
                    case 4:
                        return value.getMax();
                    default:
                        return null;
                }
            }
            if (property instanceof RandomBoolean) {
                RandomBoolean value = (RandomBoolean) property;
                switch (columnIndex) {
                    case 1:
                        return value.getProbability();
                    default:
                        return null;
                }
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (!isCellEditable(rowIndex, columnIndex) || !(aValue instanceof Float) || !valid((float) aValue, rowIndex)) {
                return;
            }
            float newValue = (float) aValue;
            Object property = getProperty(rowIndex);
            if (property instanceof RandomGaussian) {
                RandomGaussian value = (RandomGaussian) property;
                switch (columnIndex) {
                    case 1:
                        if (newValue >= value.getMin() && newValue <= value.getMax()) {
                            value.setMean((float) aValue);
                        }
                        break;
                    case 2:
                        if (newValue >= 0 && newValue <= (value.getMax() - value.getMin())) {
                            value.setStdDev((float) aValue);
                        }
                        break;
                    case 3:
                        if (newValue <= value.getMax() && newValue <= value.getMean() && value.getStdDev() <= (value.getMax() - newValue)) {
                            value.setMin((float) aValue);
                        }
                        break;
                    case 4:
                        if (newValue >= value.getMin() && newValue >= value.getMean() && value.getStdDev() <= (newValue - value.getMin())) {
                            value.setMax((float) aValue);
                        }
                        break;
                }
            } else if (property instanceof Random) {
                Random value = (Random) property;
                switch (columnIndex) {
                    case 3:
                        if (newValue <= value.getMax()) {
                            value.setMin((float) aValue);
                        }
                        break;
                    case 4:
                        if (newValue >= value.getMin()) {
                            value.setMax((float) aValue);
                        }
                        break;
                }
            } else if (property instanceof RandomBoolean) {
                RandomBoolean value = (RandomBoolean) property;
                switch (columnIndex) {
                    case 1:
                        if (newValue >= 0 && newValue <= 1) {
                            value.setProbability((float) aValue);
                        }
                }
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }

    }

    /**
     * Modelo de datos de la tabla de enemigos.
     */
    public class TableEnemiesModel extends AbstractTableModel {

        private final String[] columnNames = {"Filled", "Fill color", "Border", "Border color", "Rotate", "Clockwise", "Angular speed", "Turning speed", "Angle", "Speed", "Radius", "Sides", "Copies"};

        @Override
        public int getRowCount() {
            return list.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= getRowCount() || columnIndex >= getColumnCount()) {
                return null;
            }
            switch (columnIndex) {
                case 0:
                    return list.get(rowIndex).isFilled();
                case 1:
                    return list.get(rowIndex).getColor();
                case 2:
                    return list.get(rowIndex).hasBorder();
                case 3:
                    return list.get(rowIndex).getBorderColor();
                case 4:
                    return list.get(rowIndex).hasRotation();
                case 5:
                    return list.get(rowIndex).isClockwise();
                case 6:
                    return list.get(rowIndex).getAngularSpeed();
                case 7:
                    return list.get(rowIndex).getTurningSpeed();
                case 8:
                    return list.get(rowIndex).getInitialAngle();
                case 9:
                    return list.get(rowIndex).getSpeed();
                case 10:
                    return list.get(rowIndex).getRadius();
                case 11:
                    return list.get(rowIndex).getSides();
                case 12:
                    return list.get(rowIndex).getCopies();
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (rowIndex >= getRowCount() || columnIndex >= getColumnCount()) {
                return;
            }
            switch (columnIndex) {
                case 0:
                    list.get(rowIndex).setFilled((boolean) aValue);
                    break;
                case 1:
                    list.get(rowIndex).setColor((ColorName) aValue);
                    break;
                case 2:
                    list.get(rowIndex).setBorder((boolean) aValue);
                    break;
                case 3:
                    list.get(rowIndex).setBorderColor((ColorName) aValue);
                    break;
                case 4:
                    list.get(rowIndex).setRotation((boolean) aValue);
                    break;
                case 5:
                    list.get(rowIndex).setClockwise((boolean) aValue);
                    break;
                case 6:
                    float angularSpeed = ((Number) aValue).floatValue();
                    if (angularSpeed < 0 || angularSpeed > 9999 || angularSpeed == (float) getValueAt(rowIndex, columnIndex)) {
                        return;
                    }
                    list.get(rowIndex).setAngularSpeed(angularSpeed);
                    break;
                case 7:
                    float turningSpeed = ((Number) aValue).floatValue();
                    if (turningSpeed < 0 || turningSpeed > 9999 || turningSpeed == (float) getValueAt(rowIndex, columnIndex)) {
                        return;
                    }
                    list.get(rowIndex).setTurningSpeed(turningSpeed);
                    break;
                case 8:
                    int initialAngle = (int) aValue;
                    if (initialAngle < 0 || initialAngle > 359) {
                        return;
                    }
                    list.get(rowIndex).setInitialAngle(initialAngle);
                    break;
                case 9:
                    float speed = ((Number) aValue).floatValue();
                    if (speed < 0 || speed > 9999 || speed == (float) getValueAt(rowIndex, columnIndex)) {
                        return;
                    }
                    list.get(rowIndex).setSpeed(speed);
                    break;
                case 10:
                    int radius = (int) aValue;
                    if (radius < 1 || radius > 9999) {
                        return;
                    }
                    list.get(rowIndex).setRadius(radius);
                    break;
                case 11:
                    int sides = (int) aValue;
                    if (sides < 3 || sides > 9999) {
                        return;
                    }
                    list.get(rowIndex).setSides(sides);
                    break;
                case 12:
                    int copies = (int) aValue;
                    if (copies < 1 || copies > 9999) {
                        return;
                    }
                    list.get(rowIndex).setCopies(copies);
            }
            fireTableCellUpdated(rowIndex, columnIndex, aValue);
        }

        /**
         * Notifica de que una celda de la tabla cambió.
         */
        private void fireTableCellUpdated(int rowIndex, int columnIndex, Object value) {
            Messenger.sendMessage(list.get(rowIndex).id() + columnKeys[columnIndex], value);
        }

        /**
         * Notifica de que fue insertada una fila al final de la tabla.
         */
        public void fireTableRowAdded() {
            super.fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
        }

        /**
         * Notifica de que la fila fue eliminada.
         *
         * @param row la fila
         */
        public void fireTableRowDeleted(int row) {
            super.fireTableRowsDeleted(row, row);
        }
    }

}

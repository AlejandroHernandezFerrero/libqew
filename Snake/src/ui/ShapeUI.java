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

import game.ShapeType;
import javax.swing.JColorChooser;
import javax.swing.SpinnerNumberModel;
import libqew.ExtensibleFrame.WindowCloseListener;
import utils.ColorUtils.ColorName;
import utils.Synchronizer;

/**
 * GUI que permite editar los atributos de {@link ShapeType}.
 *
 * @author Alejandro Hernández Ferrero
 */
public class ShapeUI extends javax.swing.JPanel implements WindowCloseListener {

    private final ShapeType shape;

    /**
     * Crea una nueva GUI que modifica los atributos del tipo de figura.
     *
     * @param shape el tipo de figura
     */
    public ShapeUI(ShapeType shape) {
        super();
        initComponents();
        this.shape = shape;
        setName("Shape");
        borderColor.setBackground(shape.getBorderColor());
        fillColor.setBackground(shape.getColor());
        this.border.setSelected(shape.hasBorder());
        fill.setSelected(shape.hasBorder());
        this.borderSize.setModel(new SpinnerNumberModel(shape.getBorderWidth(), 1, 99, 1));
        this.radius.setModel(new SpinnerNumberModel(shape.getRadius(), 1, 9999, 1));
        borderStateChanged(null);
        fillStateChanged(null);
        Synchronizer.register(shape.id(), this);
    }

    @Override
    public boolean validateThis() {
        return true;
    }

    @Override
    public void cleanThis() {
        Synchronizer.unregister(this);
    }

    @Override
    public void saveThis() {
        shape.setBorderColor(borderColor.getBackground());
        shape.setColor(fillColor.getBackground());
        shape.setBorder(border.isSelected());
        shape.setFilled(fill.isSelected());
        shape.setBorderWidth((int) borderSize.getValue());
        shape.setRadius((int) radius.getValue());
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

        jPanel1 = new javax.swing.JPanel();
        border = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        labelBorderColor = new javax.swing.JLabel();
        borderColor = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        labelSize = new javax.swing.JLabel();
        borderSize = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        fill = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        labelFillColor = new javax.swing.JLabel();
        fillColor = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        radius = new javax.swing.JSpinner();
        labelRadius = new javax.swing.JLabel();

        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        layout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        setLayout(layout);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Border"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        border.setText("Enabled");
        border.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                borderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(border, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        labelBorderColor.setText("Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(labelBorderColor, gridBagConstraints);

        borderColor.setText(" ");
        borderColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borderColorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        jPanel2.add(borderColor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        labelSize.setText("Size");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel5.add(labelSize, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        jPanel5.add(borderSize, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel1, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Fill"));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        fill.setText("Enabled");
        fill.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fillStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel3.add(fill, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        labelFillColor.setText("Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel4.add(labelFillColor, gridBagConstraints);

        fillColor.setText(" ");
        fillColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillColorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        jPanel4.add(fillColor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel3.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel3, gridBagConstraints);

        jPanel6.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel6.add(radius, gridBagConstraints);

        labelRadius.setText("Radius");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel6.add(labelRadius, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 15, 15);
        add(jPanel6, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void borderColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borderColorActionPerformed
        java.awt.Color newColor = JColorChooser.showDialog(this, "Border color", borderColor.getBackground());
        if (newColor != null) {
            borderColor.setBackground(new ColorName(newColor));
        }
    }//GEN-LAST:event_borderColorActionPerformed

    private void fillColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillColorActionPerformed
        java.awt.Color newColor = JColorChooser.showDialog(this, "Fill color", fillColor.getBackground());
        if (newColor != null) {
            fillColor.setBackground(new ColorName(newColor));
        }
    }//GEN-LAST:event_fillColorActionPerformed

    private void fillStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fillStateChanged
        fillColor.setEnabled(fill.isSelected());
        labelFillColor.setEnabled(fill.isSelected());
    }//GEN-LAST:event_fillStateChanged

    private void borderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_borderStateChanged
        labelSize.setEnabled(border.isSelected());
        borderSize.setEnabled(border.isSelected());
        borderColor.setEnabled(border.isSelected());
        labelBorderColor.setEnabled(border.isSelected());
    }//GEN-LAST:event_borderStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox border;
    private javax.swing.JButton borderColor;
    private javax.swing.JSpinner borderSize;
    private javax.swing.JCheckBox fill;
    private javax.swing.JButton fillColor;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel labelBorderColor;
    private javax.swing.JLabel labelFillColor;
    private javax.swing.JLabel labelRadius;
    private javax.swing.JLabel labelSize;
    private javax.swing.JSpinner radius;
    // End of variables declaration//GEN-END:variables
}
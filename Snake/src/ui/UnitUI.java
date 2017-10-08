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

import game.UnitType;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.SpinnerNumberModel;
import libqew.ExtensibleFrame.WindowCloseListener;
import utils.Synchronizer;

/**
 * GUI que permite editar los atributos de {@link UnitType}.
 *
 * @author Alejandro Hernández Ferrero
 */
public class UnitUI extends javax.swing.JPanel implements WindowCloseListener {

    private final UnitType unit;

    /**
     * Crea una nueva GUI que modifica los atributos del tipo de unidad.
     *
     * @param unit el tipo de unidad
     */
    public UnitUI(UnitType unit) {
        super();
        initComponents();
        this.unit = unit;
        setName("Unit");
        speed.setModel(new SpinnerNumberModel(unit.getSpeed(), 0, 9999, 1));
        sides.setModel(new SpinnerNumberModel(unit.getSides(), 3, 9999, 1));
        rotate.setSelected(unit.hasRotation());
        angularSpeed.setModel(new SpinnerNumberModel(unit.getAngularSpeed(), 0, 9999, 0.1));
        clockwise.setSelected(unit.isClockwise());
        counterclockwise.setSelected(!unit.isClockwise());
        groupClockwise.add(clockwise);
        groupClockwise.add(counterclockwise);
        sliderInitialAngle.setModel(new DefaultBoundedRangeModel(unit.getInitialAngle(), 1, 0, 360));
        initialAngle.setModel(new SpinnerNumberModel(unit.getInitialAngle(), 0, 359, 1));
        rotateStateChanged(null);
        Synchronizer.register(unit.id(), this);
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
        unit.setSpeed(((Number) speed.getValue()).floatValue());
        unit.setSides((int) sides.getValue());
        unit.setRotation(rotate.isSelected());
        unit.setAngularSpeed(((Number) angularSpeed.getValue()).floatValue());
        unit.setClockwise(clockwise.isSelected());
        unit.setInitialAngle((int) initialAngle.getValue());
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

        groupClockwise = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        rotate = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        labelAngularSpeed = new javax.swing.JLabel();
        angularSpeed = new javax.swing.JSpinner();
        jPanel4 = new javax.swing.JPanel();
        clockwise = new javax.swing.JRadioButton();
        counterclockwise = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        labelSpeed = new javax.swing.JLabel();
        speed = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        labelSides = new javax.swing.JLabel();
        sides = new javax.swing.JSpinner();
        jPanel7 = new javax.swing.JPanel();
        initialAngle = new javax.swing.JSpinner();
        sliderInitialAngle = new javax.swing.JSlider();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Rotate"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        rotate.setText("Enabled");
        rotate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rotateStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(rotate, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        labelAngularSpeed.setText("Angular speed (rev/s)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel5.add(labelAngularSpeed, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        jPanel5.add(angularSpeed, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(jPanel5, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        clockwise.setText("Clockwise");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        jPanel4.add(clockwise, gridBagConstraints);

        counterclockwise.setText("Counterclockwise");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        jPanel4.add(counterclockwise, gridBagConstraints);

        jPanel1.add(jPanel4, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel1, gridBagConstraints);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        labelSpeed.setText("Speed (pixels/s)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(labelSpeed, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        jPanel2.add(speed, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        jPanel6.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        labelSides.setText("Sides");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel3.add(labelSides, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        jPanel3.add(sides, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        jPanel6.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel6, gridBagConstraints);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Initial angle"));
        jPanel7.setLayout(new java.awt.GridBagLayout());

        initialAngle.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                initialAngleStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(initialAngle, gridBagConstraints);

        sliderInitialAngle.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderInitialAngleStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(sliderInitialAngle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel7, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void sliderInitialAngleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderInitialAngleStateChanged
        initialAngle.setValue(sliderInitialAngle.getValue());
    }//GEN-LAST:event_sliderInitialAngleStateChanged

    private void initialAngleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_initialAngleStateChanged
        sliderInitialAngle.setValue((int) initialAngle.getValue());
    }//GEN-LAST:event_initialAngleStateChanged

    private void rotateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rotateStateChanged
        labelAngularSpeed.setEnabled(rotate.isSelected());
        angularSpeed.setEnabled(rotate.isSelected());
        counterclockwise.setEnabled(rotate.isSelected());
        clockwise.setEnabled(rotate.isSelected());
    }//GEN-LAST:event_rotateStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner angularSpeed;
    private javax.swing.JRadioButton clockwise;
    private javax.swing.JRadioButton counterclockwise;
    private javax.swing.ButtonGroup groupClockwise;
    private javax.swing.JSpinner initialAngle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel labelAngularSpeed;
    private javax.swing.JLabel labelSides;
    private javax.swing.JLabel labelSpeed;
    private javax.swing.JCheckBox rotate;
    private javax.swing.JSpinner sides;
    private javax.swing.JSlider sliderInitialAngle;
    private javax.swing.JSpinner speed;
    // End of variables declaration//GEN-END:variables
}

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

import game.Snake;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import libqew.ExtensibleFrame.WindowCloseListener;
import utils.Synchronizer;

/**
 * GUI que permite editar los atributos de {@link Snake}.
 *
 * @author Alejandro Hernández Ferrero
 */
public class SnakeUI extends JPanel implements WindowCloseListener {

    private final Snake snake;

    /**
     * Crea una nueva GUI que modifica los atributos de la serpiente.
     *
     * @param snake la serpiente
     */
    public SnakeUI(Snake snake) {
        super();
        this.snake = snake;
        setName("Tail");
        initComponents();
        tailSides.setModel(new SpinnerNumberModel(snake.getTailSides(), 3, 9999, 1));
        sliderTailInitialAngle.setModel(new DefaultBoundedRangeModel(snake.getTailInitialAngle(), 1, 0, 360));
        tailInitialAngle.setModel(new SpinnerNumberModel(snake.getTailInitialAngle(), 0, 360, 1));
        Synchronizer.register(snake.id(), this);
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
        snake.setTailSides((int) tailSides.getValue());
        snake.setTailInitialAngle((int) tailInitialAngle.getValue());
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
        jPanel3 = new javax.swing.JPanel();
        tailSides = new javax.swing.JSpinner();
        labelTailSides = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        tailInitialAngle = new javax.swing.JSpinner();
        sliderTailInitialAngle = new javax.swing.JSlider();
        labelTailInitialAngle = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Tail"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(tailSides, gridBagConstraints);

        labelTailSides.setText("Sides");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(labelTailSides, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPanel3, gridBagConstraints);

        jPanel7.setLayout(new java.awt.GridBagLayout());

        tailInitialAngle.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tailInitialAngleStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(tailInitialAngle, gridBagConstraints);

        sliderTailInitialAngle.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderTailInitialAngleStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(sliderTailInitialAngle, gridBagConstraints);

        labelTailInitialAngle.setText("Initial angle");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel7.add(labelTailInitialAngle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void sliderTailInitialAngleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderTailInitialAngleStateChanged
        tailInitialAngle.setValue(sliderTailInitialAngle.getValue());
    }//GEN-LAST:event_sliderTailInitialAngleStateChanged

    private void tailInitialAngleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tailInitialAngleStateChanged
        sliderTailInitialAngle.setValue((int) tailInitialAngle.getValue());
    }//GEN-LAST:event_tailInitialAngleStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel labelTailInitialAngle;
    private javax.swing.JLabel labelTailSides;
    private javax.swing.JSlider sliderTailInitialAngle;
    private javax.swing.JSpinner tailInitialAngle;
    private javax.swing.JSpinner tailSides;
    // End of variables declaration//GEN-END:variables
}
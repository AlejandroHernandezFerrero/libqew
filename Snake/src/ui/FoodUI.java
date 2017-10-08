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

import game.FoodType;
import javax.swing.SpinnerNumberModel;
import libqew.ExtensibleFrame.WindowCloseListener;
import utils.Synchronizer;

/**
 * GUI que permite editar los atributos de {@link FoodType}.
 *
 * @author Alejandro Hernández Ferrero
 */
public class FoodUI extends javax.swing.JPanel implements WindowCloseListener {

    private final FoodType food;

    /**
     * Crea una nueva GUI que modifica los atributos del tipo de comida.
     *
     * @param food el tipo de comida
     */
    public FoodUI(FoodType food) {
        super();
        initComponents();
        this.food = food;
        maximum.setModel(new SpinnerNumberModel(food.getMaximum(), 1, 99999, 1));
        spawnTime.setModel(new SpinnerNumberModel(food.getSpawnTime(), 0, 99999, 1));
        Synchronizer.register(food.id(), this);
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
        food.setMaximum((int) maximum.getValue());
        food.setSpawnTime(((Double) spawnTime.getValue()).floatValue());
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
        labelMaximum = new javax.swing.JLabel();
        maximum = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        labelSpawnTime = new javax.swing.JLabel();
        spawnTime = new javax.swing.JSpinner();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        labelMaximum.setText("Maximum");
        jPanel1.add(labelMaximum, new java.awt.GridBagConstraints());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel1.add(maximum, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 15, 15);
        add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        labelSpawnTime.setText("Spawn time (s)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel2.add(labelSpawnTime, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel2.add(spawnTime, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 15, 15);
        add(jPanel2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel labelMaximum;
    private javax.swing.JLabel labelSpawnTime;
    private javax.swing.JSpinner maximum;
    private javax.swing.JSpinner spawnTime;
    // End of variables declaration//GEN-END:variables
}

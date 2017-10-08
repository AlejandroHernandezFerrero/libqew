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
package testb;

import java.awt.Color;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import libqew.ExtensibleFrame.WindowCloseListener;

/**
 *
 * @author Alejandro
 */
public class PanelBase extends javax.swing.JPanel implements WindowCloseListener {

    /**
     * Creates new form Panel
     */
    public PanelBase() {
        initComponents();
        clave.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                PanelBase.super.setName(clave.getText());
                clave.grabFocus();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                PanelBase.super.setName(clave.getText());
                clave.grabFocus();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                PanelBase.super.setName(clave.getText());
                clave.grabFocus();
            }
        });
    }

    @Override
    public void cleanThis() {
    }

    @Override
    public void setName(String name) {
        clave.setText(name);
    }

    @Override
    public void saveThis() {
    }

    @Override
    public boolean validateThis() {
        if (valor.getText().isEmpty()) {
            setBackground(Color.RED);
            return false;
        } else {
            setBackground(Color.GREEN);
            return true;
        }
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

        labelClave = new javax.swing.JLabel();
        clave = new javax.swing.JTextField();
        labelValor = new javax.swing.JLabel();
        valor = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new java.awt.GridBagLayout());

        labelClave.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelClave.setText("Clave");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(labelClave, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.1;
        add(clave, gridBagConstraints);

        labelValor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelValor.setText("Valor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(labelValor, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.1;
        add(valor, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField clave;
    private javax.swing.JLabel labelClave;
    private javax.swing.JLabel labelValor;
    private javax.swing.JTextField valor;
    // End of variables declaration//GEN-END:variables
}

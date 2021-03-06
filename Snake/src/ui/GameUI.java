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

import game.Game;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JColorChooser;
import javax.swing.SpinnerNumberModel;
import libqew.ExtensibleFrame.WindowCloseListener;
import libqew.TreeNodePanel;
import utils.ColorUtils.ColorName;
import utils.Synchronizer;

/**
 * GUI que permite editar los atributos de {@link Game}.
 *
 * @author Alejandro Hernández Ferrero
 */
public class GameUI extends TreeNodePanel implements WindowCloseListener {

    private final Game game;

    /**
     * Crea una nueva GUI que modifica los parámetros del juego.
     *
     * @param game el juego
     */
    public GameUI(Game game) {
        this.game = game;
        initComponents();
        setName("Game");
        gameBackground.setBackground(game.getGameBackground());
        sliderfps.setModel(new DefaultBoundedRangeModel((int) Math.min(60, 1000 / game.getMS()), 1, 5, 201));
        fps.setModel(new SpinnerNumberModel((int) Math.min(60, 1000 / game.getMS()), 5, 200, 1));
        sliderWidth.setModel(new DefaultBoundedRangeModel(game.getResolution().width, 1, 400, 4001));
        width.setModel(new SpinnerNumberModel(game.getResolution().width, 100, 4000, 20));
        sliderHeight.setModel(new DefaultBoundedRangeModel(game.getResolution().height, 1, 400, 4001));
        height.setModel(new SpinnerNumberModel(game.getResolution().height, 100, 4000, 20));
        showfps.setSelected(game.isShowFPS());
        Synchronizer.register(-1, this);
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
        game.setMS(1000f / (int) fps.getValue());
        game.setGameBackground(gameBackground.getBackground());
        game.getResolution().setSize((int) width.getValue(), (int) height.getValue());
        game.setShowFPS(showfps.isSelected());
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

        jPanel4 = new javax.swing.JPanel();
        labelBackground = new javax.swing.JLabel();
        gameBackground = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        sliderHeight = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        height = new javax.swing.JSpinner();
        width = new javax.swing.JSpinner();
        labelResolution = new javax.swing.JLabel();
        sliderWidth = new javax.swing.JSlider();
        jPanel9 = new javax.swing.JPanel();
        fps = new javax.swing.JSpinner();
        sliderfps = new javax.swing.JSlider();
        showfps = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        labelBackground.setText("Background");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel4.add(labelBackground, gridBagConstraints);

        gameBackground.setText(" ");
        gameBackground.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gameBackgroundActionPerformed(evt);
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
        jPanel4.add(gameBackground, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        add(jPanel4, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Resolution"));
        jPanel8.setLayout(new java.awt.GridBagLayout());

        sliderHeight.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderHeightStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(sliderHeight, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        height.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                heightStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(height, gridBagConstraints);

        width.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                widthStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(width, gridBagConstraints);

        labelResolution.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel1.add(labelResolution, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jPanel1, gridBagConstraints);

        sliderWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderWidthStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(sliderWidth, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel8, gridBagConstraints);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "FPS"));
        jPanel9.setLayout(new java.awt.GridBagLayout());

        fps.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fpsStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(fps, gridBagConstraints);

        sliderfps.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderfpsStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(sliderfps, gridBagConstraints);

        showfps.setText("Show");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(showfps, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel9, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void gameBackgroundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameBackgroundActionPerformed
        java.awt.Color newColor = JColorChooser.showDialog(this, "Background color", gameBackground.getBackground());
        if (newColor != null) {
            gameBackground.setBackground(new ColorName(newColor));
        }
    }//GEN-LAST:event_gameBackgroundActionPerformed

    private void sliderHeightStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderHeightStateChanged
        height.setValue(sliderHeight.getValue());
    }//GEN-LAST:event_sliderHeightStateChanged

    private void widthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_widthStateChanged
        sliderWidth.setValue((int) width.getValue());
    }//GEN-LAST:event_widthStateChanged

    private void heightStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_heightStateChanged
        sliderHeight.setValue((int) height.getValue());
    }//GEN-LAST:event_heightStateChanged

    private void sliderWidthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderWidthStateChanged
        width.setValue(sliderWidth.getValue());
    }//GEN-LAST:event_sliderWidthStateChanged

    private void fpsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fpsStateChanged
        sliderfps.setValue((int) fps.getValue());
    }//GEN-LAST:event_fpsStateChanged

    private void sliderfpsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderfpsStateChanged
        fps.setValue(sliderfps.getValue());
    }//GEN-LAST:event_sliderfpsStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner fps;
    private javax.swing.JButton gameBackground;
    private javax.swing.JSpinner height;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel labelBackground;
    private javax.swing.JLabel labelResolution;
    private javax.swing.JCheckBox showfps;
    private javax.swing.JSlider sliderHeight;
    private javax.swing.JSlider sliderWidth;
    private javax.swing.JSlider sliderfps;
    private javax.swing.JSpinner width;
    // End of variables declaration//GEN-END:variables
}

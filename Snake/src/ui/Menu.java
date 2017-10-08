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
import game.FoodType;
import game.Game;
import game.Snake;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import libqew.ExtensibleFrame;
import libqew.ExtensibleFrame.WindowCloseListener;
import libqew.TreeNodePanel;
import libqew.TreeViewPanel;
import resources.Resources;

/**
 * Barra de menú del juego. Permite que se abran múltiples instancias de la
 * misma GUI al mismo tiempo.
 *
 * @author Alejandro Hernández Ferrero
 */
public class Menu extends JMenuBar {

    /**
     * Crea una nueva barra de menú que permite editar los componentes del
     * juego.
     *
     * @param game el juego
     * @param snake la serpiente
     * @param enemies los enemigos
     * @param food la comida
     */
    public Menu(Game game, Snake snake, Enemies enemies, FoodType food) {
        super();
        JMenuItem buttonSnake = new JMenuItem("Snake");
        JMenuItem buttonEnemies = new JMenuItem("Enemies");
        JMenuItem buttonFood = new JMenuItem("Food");
        JMenuItem buttonOptions = new JMenuItem("Options");
        JMenuItem buttonReset = new JMenuItem("Reset");
        JMenuItem buttonPause = new JMenuItem("Pause");
        JMenuItem buttonQuit = new JMenuItem("Quit");
        setLayout(new BorderLayout());
        Box left = new Box(BoxLayout.X_AXIS);
        Box right = new Box(BoxLayout.X_AXIS);
        buttonSnake.setHorizontalAlignment(SwingConstants.CENTER);
        buttonEnemies.setHorizontalAlignment(SwingConstants.CENTER);
        buttonFood.setHorizontalAlignment(SwingConstants.CENTER);
        buttonOptions.setHorizontalAlignment(SwingConstants.CENTER);
        buttonReset.setHorizontalAlignment(SwingConstants.CENTER);
        buttonPause.setHorizontalAlignment(SwingConstants.CENTER);
        buttonQuit.setHorizontalAlignment(SwingConstants.CENTER);
        left.add(buttonSnake);
        left.add(buttonEnemies);
        left.add(buttonFood);
        left.add(buttonOptions);
        right.add(buttonPause);
        right.add(buttonReset);
        right.add(buttonQuit);
        add(left, BorderLayout.WEST);
        add(Box.createGlue());
        add(right, BorderLayout.EAST);
        WindowCloseListener listener = new WindowCloseListener() { // Listener que se ejecutará al cerrar cada GUI
            @Override
            public boolean validateThis() {
                return true;
            }

            @Override
            public void saveThis() {
                game.reset(); // Si se cambiaron los datos se crea una nueva partida con ellos
            }

            @Override
            public void cleanThis() {

            }

        };
        buttonSnake.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.pause();
                ExtensibleFrame.showWindow(snake.getUI(), Resources.getIcons()).setWindowCloseListener(listener);
            }
        });
        buttonEnemies.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.pause();
                ExtensibleFrame.showWindow(enemies.getUI(), Resources.getIcons()).setWindowCloseListener(listener);
            }
        });
        buttonFood.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.pause();
                ExtensibleFrame.showWindow(food.getUI(), Resources.getIcons()).setWindowCloseListener(listener);
            }
        });
        buttonOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreeNodePanel gameui = new GameUI(game);
                gameui.addChild(snake.getUI());
                gameui.addChild(food.getUI());
                gameui.addChild(enemies.getUI());
                gameui.setName("Game");
                game.pause();
                ExtensibleFrame.showWindow(gameui, Resources.getIcons()).setWindowCloseListener(listener);
            }
        });
        buttonReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.pause();
                game.reset();
            }
        });
        buttonPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.pause();
            }
        });
        buttonQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.dispatchEvent(new WindowEvent(game, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

}

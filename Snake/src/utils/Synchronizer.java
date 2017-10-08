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

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Sincroniza grupos de elementos gráficos registrados con una misma clave.
 *
 * @author Alejandro Hernández Ferrero
 */
public class Synchronizer {

    private static final HashMap<Object, ArrayList<Listener>> REGISTERED_LISTENERS = new HashMap<>(0); // Listeners registrados
    private static final HashMap<Object, HashSet<String>> OBJECTS_KEYS = new HashMap<>(0); // Claves que registra cada grupo
    private static final HashMap<String, HashSet<Object>> KEYS_OBJECTS = new HashMap<>(0); // Grupos que registran cada clave

    /**
     * Registra un grupo de elementos gráficos asociados a un identificador para
     * que sean sincronizados con otros elementos registrados bajo la misma
     * clave. El grupo debe ser un objeto que tenga cada elemento como un campo
     * y con el mismo nombre que el elemento con el que se desea sincronizar en
     * los demás grupos registrados con el mismo identificador.
     * <p>
     * Es decir, la clave de cada elemento será:
     * <code>identificador del grupo + nombre del campo del elemento</code> y
     * todos los elementos con una clave idéntica estarán sincronizados.
     *
     * @param id el identificador del grupo
     * @param object un objeto que contiene los elementos como campos
     */
    public static synchronized void register(int id, Object object) {
        HashSet<String> keys = OBJECTS_KEYS.get(object);
        if (keys == null) { // Primera vez que se registra el objeto (puede registrarse varias veces con distintas ids)
            keys = new HashSet<>();
            OBJECTS_KEYS.put(object, keys);
        }
        // Reflexión para obtener los elementos de los campos
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String key = id + field.getName(); // Forma la clave del campo
                // Solo implementado para estos tipos de elementos
                if (JButton.class.isAssignableFrom(field.getType())) {
                    JButton button = (JButton) field.get(object);
                    if (button.getBackground() instanceof ColorUtils.ColorName) { // Botón seleccionador de colores
                        // Si cuando se registra ya hay elementos sincronizados se inicializa con el valor común
                        Object message = Mailbox.receiveMessage(key);
                        if (message != null) {
                            button.setBackground((Color) message);
                        }
                        // Sincroniza el elemento mediante la clave
                        new Listener(key, object) { // Cambia su valor cuando otro cambia
                            @Override
                            public void receive(Object key, Object message) {
                                if (!button.getBackground().equals(message)) {
                                    button.setBackground((java.awt.Color) message);
                                }
                            }
                        };
                        // Avisa a los demás para que se sincronicen al cambio
                        button.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                Messenger.sendMessage(key, button.getBackground()); // Aviso directo a los ya registrados
                                Mailbox.sendMessage(key, button.getBackground()); // Aviso indirecto para los que aún no estén registrados
                            }
                        });
                    }
                } else if (JSlider.class.isAssignableFrom(field.getType())) { // JSlider
                    JSlider comp = (JSlider) field.get(object);
                    Object message = Mailbox.receiveMessage(key);
                    if (message != null) {
                        comp.setValue((int) message);
                    }
                    new Listener(key, object) {
                        @Override
                        public void receive(Object key, Object message) {
                            if (comp.getValue() != (int) message) {
                                comp.setValue((int) message);
                            }
                        }
                    };
                    comp.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            Messenger.sendMessage(key, comp.getValue());
                            Mailbox.sendMessage(key, comp.getValue());
                        }
                    });
                } else if (JSpinner.class.isAssignableFrom(field.getType())) { // JSpinner
                    JSpinner comp = (JSpinner) field.get(object);
                    Object message = Mailbox.receiveMessage(key);
                    if (message != null) {
                        comp.setValue(message);
                    }
                    new Listener(key, object) {
                        @Override
                        public void receive(Object key, Object message) {
                            if (!comp.getValue().equals(message)) {
                                comp.setValue(message);
                            }
                        }
                    };
                    comp.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            Messenger.sendMessage(key, comp.getValue());
                            Mailbox.sendMessage(key, comp.getValue());
                        }
                    });
                } else if (JToggleButton.class.isAssignableFrom(field.getType())) { // JToggleButton y todas sus subclases
                    JToggleButton comp = (JToggleButton) field.get(object);
                    Object message = Mailbox.receiveMessage(key);
                    if (message != null) {
                        comp.setSelected((boolean) message);
                    }
                    new Listener(key, object) {
                        @Override
                        public void receive(Object key, Object message) {
                            if (comp.isSelected() != (boolean) message) {
                                comp.setSelected((boolean) message);
                            }
                        }
                    };
                    comp.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            Messenger.sendMessage(key, comp.isSelected());
                            Mailbox.sendMessage(key, comp.isSelected());
                        }
                    });
                } else {
                    field.setAccessible(false);
                    continue; // Si el elemento del campo no está implementado no se añade su clave
                }
                field.setAccessible(false);
                keys.add(key); // Añade la clave del elemento sincronizado
                // Añade el objeto al conjunto de objetos que utilizan la clave
                HashSet<Object> objects = KEYS_OBJECTS.get(key);
                if (objects == null) { // Si es el primero
                    objects = new HashSet<>();
                    KEYS_OBJECTS.put(key, objects);
                }
                objects.add(object);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(ColorUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Desincroniza los elementos asociados al objeto.
     *
     * @param object el objeto
     */
    public static synchronized void unregister(Object object) {
        HashSet<String> keys = OBJECTS_KEYS.get(object); // Claves que usa el objeto
        if (keys != null) {
            for (String key : keys) {
                HashSet<Object> objects = KEYS_OBJECTS.get(key); // Objetos que usan la clave
                if (objects != null) {
                    objects.remove(object); // Elimina el objeto de la lista
                    if (objects.isEmpty()) { // Si era el último
                        Mailbox.removeMessage(key); // Elimina el mensaje asíncrono
                        KEYS_OBJECTS.remove(key);
                    }
                }
            }
        }
        OBJECTS_KEYS.remove(object);
        // Elimina los listeners asociados al objeto
        ArrayList<Listener> list = REGISTERED_LISTENERS.get(object);
        if (list != null) {
            for (Listener listener : list) {
                listener.remove();
            }
        }
        REGISTERED_LISTENERS.remove(object);
    }

    /**
     * Listener que recibe eventos asociados a una clave.
     */
    public static abstract class Listener implements Messenger.MessageReceiver {

        private final Object key;

        /**
         * Crea un nuevo listener bajo la responsabilidad del objeto que recibe
         * eventos asociados a la clave. Este listener será eliminado
         * automáticamente cuando se desregistre el objeto que es responsable de
         * él mediante {@link Synchronizer#unregister}.
         *
         * @param key la clave
         * @param object el objeto responsable
         */
        public Listener(Object key, Object object) {
            this.key = key;
            // Añade este listener a la lista de listeners del objeto que es responsable de él
            ArrayList<Listener> list = REGISTERED_LISTENERS.get(object);
            if (list == null) {
                list = new ArrayList<>();
                REGISTERED_LISTENERS.put(object, list);
            }
            list.add(this);
            Messenger.addListener(key, this);
        }

        /**
         * Elimina este listener del paso de mensajes.
         */
        private void remove() {
            Messenger.removeListener(key, this);
        }

    }
}

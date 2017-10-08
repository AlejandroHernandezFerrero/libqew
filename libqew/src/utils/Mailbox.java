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

import java.util.HashMap;

/**
 * Proporciona un mecanismo para paso de mensajes de forma asíncrona.
 * <p>
 * Los mensajes enviados permanecerán guardados hasta que se borren y se podrá
 * acceder a ellos durante ese tiempo todas las veces que se desee.
 * <p>
 * Permite ser usado globalmente mediante sus métodos estáticos o en un ámbito
 * reducido creando y usando una instancia particular. De esta forma podrían
 * convivir varias instancias, cada una con su propio espacio de claves, lo que
 * evita conflictos si coincide alguna clave entre ellas y proporciona control y
 * seguridad al restringir su visibilidad.
 *
 * @author Alejandro Hernández Ferrero
 */
public class Mailbox {

    private static Mailbox instance;

    private final HashMap<Object, Object> mails;

    /**
     * Crea una instancia que permite reducir el ámbito de los mensajes a las
     * clases que la usen.
     */
    public Mailbox() {
        this.mails = new HashMap(0);
    }

    /**
     * Envía un mensaje asociado a una clave, que permanecerá guardado hasta que
     * se borre.
     *
     * @param key la clave
     * @param message el mensaje
     */
    public synchronized void send(Object key, Object message) {
        mails.put(key, message);
    }

    /**
     * Obtiene el mensaje guardado asociado a la clave.
     *
     * @param key la clave
     * @return el mensaje guardado asociado, <code>null</code> si no existe
     */
    public synchronized Object receive(Object key) {
        return mails.get(key);
    }

    /**
     * Elimina el mensaje guardado asociado a la clave.
     *
     * @param key la clave
     */
    public synchronized void remove(Object key) {
        mails.remove(key);
    }

    /**
     * Envía un mensaje global asociado a una clave, que permanecerá guardado
     * hasta que se borre.
     *
     * @param key la clave
     * @param message el mensaje
     */
    public static void sendMessage(Object key, Object message) {
        getInstance().send(key, message);
    }

    /**
     * Obtiene el mensaje global guardado asociado a la clave.
     *
     * @param key la clave
     * @return el mensaje guardado asociado, <code>null</code> si no existe
     */
    public static Object receiveMessage(Object key) {
        return getInstance().receive(key);
    }

    /**
     * Elimina el mensaje global guardado asociado a la clave.
     *
     * @param key la clave
     */
    public static void removeMessage(Object key) {
        getInstance().remove(key);
    }

    /**
     * Obtiene la instancia global.
     */
    private synchronized static Mailbox getInstance() {
        if (instance == null) {
            instance = new Mailbox();
        }
        return instance;
    }
}

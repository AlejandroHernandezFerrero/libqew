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
import java.util.LinkedHashSet;

/**
 * Proporciona un mecanismo para paso de mensajes de forma síncrona.
 * <p>
 * Los mensajes enviados serán redirigidos directamente a los {@link MessageReceiver} asociados y no
 * se guardarán en memoria.
 * <p>
 * Permite ser usado globalmente mediante sus métodos estáticos o en un ámbito reducido creando y
 * usando una instancia particular. De esta forma podrían convivir varias instancias, cada una con
 * su propio espacio de claves, lo que evita conflictos si coincide alguna clave entre ellas y
 * proporciona control y seguridad al restringir su visibilidad.
 *
 * @author Alejandro Hernández Ferrero
 */
public class Messenger {

  private static Messenger instance;

  private final HashMap<Object, LinkedHashSet<MessageReceiver>> receivers;

  /**
   * Crea una instancia que permite reducir el ámbito de los mensajes a las clases que la usen.
   */
  public Messenger() {
    this.receivers = new HashMap(0);
  }

  /**
   * Envía un mensaje asociado a una clave.
   *
   * @param key la clave
   * @param message el mensaje
   */
  public synchronized void send(Object key, Object message) {
    LinkedHashSet<MessageReceiver> list = receivers.get(key);
    if (list != null) { // Si tiene listeners asociados
      for (MessageReceiver receiver : list) {
        receiver.receive(key, message); // Se les redirige el mensaje
      }
    }
  }

  /**
   * Añade un listener para que reciba los mensajes asociados con la clave que se envíen.
   *
   * @param key la clave
   * @param receiver el listener que recibirá los mensajes
   */
  public synchronized void add(Object key, MessageReceiver receiver) {
    LinkedHashSet<MessageReceiver> list = receivers.get(key);
    if (list == null) { // Si es el primero se crea un nuevo conjunto
      list = new LinkedHashSet<>(2);
      receivers.put(key, list);
    }
    list.add(receiver);
  }

  /**
   * Elimina un listener para no que reciba más mensajes asociados con la clave.
   *
   * @param key la clave
   * @param receiver el listener a eliminar
   */
  public synchronized void remove(Object key, MessageReceiver receiver) {
    LinkedHashSet<MessageReceiver> list = receivers.get(key);
    if (list == null) { // No hay listeners con esa clave así que no se hace nada
      return;
    }
    list.remove(receiver);
    if (list.isEmpty()) { // Si el conjunto queda vacío se quita
      receivers.remove(key);
    }
  }

  /**
   * Envía un mensaje global asociado a una clave.
   *
   * @param key la clave
   * @param message el mensaje
   */
  public static void sendMessage(Object key, Object message) {
    getInstance().send(key, message);
  }

  /**
   * Añade un listener para que reciba los mensajes globales asociados con la clave que se envíen.
   *
   * @param key la clave
   * @param receiver el listener que recibirá los mensajes
   */
  public static void addListener(Object key, MessageReceiver receiver) {
    getInstance().add(key, receiver);
  }

  /**
   * Elimina un listener para no que reciba más mensajes globales asociados con la clave.
   *
   * @param key la clave
   * @param receiver el listener a eliminar
   */
  public static void removeListener(Object key, MessageReceiver receiver) {
    getInstance().remove(key, receiver);
  }

  /**
   * Obtiene la instancia global.
   */
  private synchronized static Messenger getInstance() {
    if (instance == null) {
      instance = new Messenger();
    }
    return instance;
  }

  /**
   * Interfaz que deben implementar los listeners para recibir mensajes.
   */
  public interface MessageReceiver {

    /**
     * Recibe un mensaje asociado con la clave.
     * <p>
     * Este método será llamado siempre que se envíe un mensaje asociado con una clave para la que
     * esté registrado este listener en un {@link Messenger}.
     *
     * @param key la clave
     * @param message el mensaje
     */
    public void receive(Object key, Object message);

  }

}

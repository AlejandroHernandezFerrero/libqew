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
package libqew;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * GUI extensible en vista de árbol.
 * <p>
 * Permite navegar por una jerarquía de GUIs en la parte izquierda y seleccionar
 * una de ellas, que se mostrará en la parte derecha. El tamaño de la GUI se
 * autoajustará para adaptarse a su contenido cada vez que un nodo cambie, se
 * seleccione otro nodo, o se expandan, colapsen, inserten o eliminen nodos en
 * la jerarquía. La raíz será gestionada internamente y no se mostrará, pero sí
 * su contenido si no hay ningún nodo seleccionado.
 * <p>
 * Aunque el árbol permite cualquier {@link MutableTreeNode} como nodo, debido a
 * que a esta interfaz le falta un método para obtener su contenido, solo será
 * visible el contenido de nodos que implementen la interfaz creada al respecto:
 * {@link TreeNode}, o bien sean subclase de {@link DefaultMutableTreeNode}. Se
 * recomienda el uso de {@link Node}, que extiende
 * {@link DefaultMutableTreeNode} con métodos útiles e informa automáticamente a
 * la GUI de los eventos que le afectan.
 *
 * @author Alejandro Hernández Ferrero
 */
public class TreeViewPanel extends ExtensiblePanel {

    private final Root root;
    private final JTree tree;
    private final DefaultTreeModel model;
    private final JSplitPane splitPane;
    private JPanel left;
    private Component bottom;
    private Component top;
    private boolean empty;
    private int maxRows = 10;

    /**
     * Crea una GUI con vista de árbol vacía, a la que se le pueden añadir
     * componentes posteriormente.
     */
    public TreeViewPanel() {
        super();
        super.setLayout(new CardLayout());
        root = new Root();
        model = new DefaultTreeModel(root);
        tree = new JTree();
        tree.setVisibleRowCount(0);
        tree.setRootVisible(false);
        empty = true;
        JScrollPane treeScrollPane = new JScrollPane(tree);
        left = new JPanel(new BorderLayout());
        left.add(treeScrollPane, BorderLayout.CENTER);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, (Component) root.getUserObject());

        tree.addTreeSelectionListener(new TreeSelectionListener() { // Cuando se selecciona un nodo
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object object = tree.getLastSelectedPathComponent();
                if (object == null) { // Si no hay selección no se hace nada
                    return;
                }
                Object userObject;
                // Se da compatibilidad con estos 2 tipos de nodo
                if (object instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
                    userObject = node.getUserObject();
                } else if (object instanceof TreeNode) {
                    TreeNode node = (TreeNode) object;
                    userObject = node.getUserObject();
                } else { // Nodo no válido
                    return;
                }
                if (userObject != null && userObject instanceof Component) { // Se comprueba que el contenido del nodo es válido
                    Component comp = (Component) userObject;
                    splitPane.setRightComponent(comp); // Muestra el componente seleccionado
                    pack(); // Autoajusta la GUI al nuevo contenido
                    tree.scrollPathToVisible(tree.getSelectionPath()); // Centra la vista de la jerarquía en la selección
                }
            }
        });
        tree.addTreeExpansionListener(new TreeExpansionListener() { // Cuando se expande/colapsa un nodo
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                boundVisibleRowCount(); // Ajusta el número de filas a mostrar
                splitPane.resetToPreferredSizes(); // Se reajusta
                pack(); // Avisa a la ventana para que se reajuste
                tree.scrollPathToVisible(event.getPath()); // Centra la vista en el nodo expandido
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                boundVisibleRowCount();
                splitPane.resetToPreferredSizes();
                pack();
            }
        });
        model.addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                // Si un nodo cambiado es el seleccionado se recarga su contenido
                Object selected = tree.getLastSelectedPathComponent();
                for (Object child : e.getChildren()) {
                    if (child.equals(selected)) {
                        Object userObject = null;
                        if (selected instanceof DefaultMutableTreeNode) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selected;
                            userObject = node.getUserObject();
                        } else if (selected instanceof TreeNode) {
                            TreeNode node = (TreeNode) selected;
                            userObject = node.getUserObject();
                        }
                        if (userObject != null && userObject instanceof Component) {
                            splitPane.setRightComponent((Component) userObject);
                        }
                        break;
                    }
                }
                boundVisibleRowCount();
                splitPane.resetToPreferredSizes();
                pack();
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {
                if (empty) { // Si estaba vacío
                    tree.setRootVisible(true); // Se muestra solo momentáneamente para poder expandirla
                    tree.expandRow(0); // Expande la raíz
                    tree.setSelectionRow(1); // Selecciona el nodo insertado
                    tree.setRootVisible(false); // Oculta la raíz
                    empty = false;
                    TreeViewPanel.super.addImpl(splitPane, null, -1); // Se añade de forma perezosa para que sea detectado correctamente por el GUI builder
                }
                boundVisibleRowCount();
                splitPane.resetToPreferredSizes();
                pack();
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
                if (root.isLeaf()) { // Si ha quedado vacío
                    empty = true;
                    /*
                     * Si se eliminan todos los nodos se quita también el panel
                     * interno y se deberá insertar de nuevo al añadir un nodo
                     * para que sea detectado correctamente por el GUI builder.
                     */
                    TreeViewPanel.super.remove(0);
                }
                boundVisibleRowCount();
                splitPane.setRightComponent(getSelectedComponent());
                splitPane.resetToPreferredSizes();
                pack();
            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {
                boundVisibleRowCount();
                splitPane.resetToPreferredSizes();
                pack();
            }
        });
        // Hay que añadir el modelo al árbol después de añadir el anterior listener al modelo, 
        // ya que al añadirlo el árbol registra su propio listener pero los listeners se disparan en orden inverso, 
        // con lo que así se evita que se llame a este listener antes que al del árbol y se vea por lo tanto el árbol desactualizado en ese instante
        tree.setModel(model);
        // Este listener se añade después para que sea llamado antes de modificar el árbol
        // y de esta forma poder cambiar la selección si el nodo que se va a borrar está seleccionado
        model.addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
                for (Object ch : e.getChildren()) { // Para cada nodo eliminado
                    // Hay que comprobar si el que se va a borrar es el seleccionado para en ese caso seleccionar otro
                    if (ch.equals(tree.getLastSelectedPathComponent())) { // Si el que se va a eliminar es el que está seleccionado
                        int index = tree.getMinSelectionRow(); // Índice del nodo seleccionado
                        if (index > 0) { // Si no es el primero se selecciona el anterior
                            tree.setSelectionRow(index - 1);
                            splitPane.setRightComponent((Component) ((TreeNode) tree.getLastSelectedPathComponent()).getUserObject());
                        } else if (index < tree.getRowCount() - 1) { // Si es el primero pero no es el último se selecciona el siguiente
                            tree.setSelectionRow(index + 1);
                            splitPane.setRightComponent((Component) ((TreeNode) tree.getLastSelectedPathComponent()).getUserObject());
                        } else { // Si no, es que quedará vacío así que se selecciona la raíz
                            tree.clearSelection();
                            splitPane.setRightComponent((Component) getRoot().getUserObject());
                        }
                    }
                }
            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {
            }
        });
    }

    /**
     * Este método se ha sobreescrito vacío y no hará nada, ya que esta clase
     * usa un layout concreto y no debe cambiarse.
     */
    @Override
    public void setLayout(LayoutManager mgr) {
    }

    /**
     * Obtiene el nodo raíz, que es generado por el árbol y no se muestra. Los
     * nodos que inserte el usuario directamente en el árbol serán añadidos como
     * hijos de este nodo.
     *
     * @return el nodo raíz
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Obtiene el componente seleccionado.
     *
     * @return el componente seleccionado
     */
    public Component getSelectedComponent() {
        return splitPane.getRightComponent();
    }

    /**
     * Obtiene el nodo seleccionado.
     *
     * @return el nodo seleccionado
     */
    public MutableTreeNode getSelectedNode() {
        return (MutableTreeNode) tree.getLastSelectedPathComponent();
    }

    /**
     * Establece el límite de nodos para los que se reservará espacio en la
     * vista de la jerarquía.
     * <p>
     * Si el árbol tiene menos nodos visibles que este límite se mostrarán todos
     * sin barras de scroll, si entran en pantalla. Si tiene más, se intentará
     * mostrar ese número de nodos y se añadirán barras de scroll para poder ver
     * el resto. Hay que tener en cuenta que ese tamaño será únicamente el
     * preferido de la jerarquía de nodos, y que el tamaño de la GUI depende de
     * otros componentes, por lo que si el componente seleccionado es más
     * grande, la jerarquía de nodos se expandirá para llenar el espacio
     * disponible y podría aumentar el número de nodos visibles.
     * <p>
     * El valor por defecto es 10.
     *
     * @param maxRows el límite de nodos a intentar mostrar a la vez en la
     * jerarquía
     */
    public void setMaximumVisibleRows(int maxRows) {
        this.maxRows = maxRows;
    }

    /**
     * Obtiene el límite de nodos para los que se reservará espacio en la vista
     * de la jerarquía.
     *
     * @return el límite establecido
     */
    public int getMaximumVisibleRows() {
        return maxRows;
    }

    /**
     * Establece un título para mostrar por encima de la jerarquía de nodos.
     * <p>
     * Hace uso de {@link #setTopComponent} por lo que comparte sus
     * restricciones en cuanto a uso del espacio.
     *
     * @param title el título
     */
    public void setTitle(String title) {
        if (title == null) {
            return;
        }
        JLabel label = new JLabel(title);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setTopComponent(label);
    }

    /**
     * Permite establecer un componente personalizado que se mostrará por encima
     * de la jerarquía de nodos.
     * <p>
     * Llamadas posteriores a este método eliminarán el componente anterior, por
     * lo que si se desea añadir varios deberán ser añadidos todos en un
     * contenedor e insertar dicho contenedor aquí.
     * <p>
     * Adecuado para mostrar un título mediante {@link JLabel}.
     *
     * @param comp el componente
     */
    public void setTopComponent(Component comp) {
        if (comp == null) {
            return;
        }
        if (top != null) {
            left.remove(top);
        }
        top = comp;
        left.add(comp, BorderLayout.NORTH);
    }

    /**
     * Permite establecer un componente personalizado que se mostrará por debajo
     * de la jerarquía de nodos.
     * <p>
     * Llamadas posteriores a este método eliminarán el componente anterior, por
     * lo que si se desea añadir varios deberán ser añadidos todos en un
     * contenedor e insertar dicho contenedor aquí.
     * <p>
     * Adecuado para mostrar un panel de controles que permitan modificar la
     * jerarquía de nodos.
     *
     * @param comp el componente
     */
    public void setBottomComponent(Component comp) {
        if (comp == null) {
            return;
        }
        if (bottom != null) {
            left.remove(bottom);
        }
        bottom = comp;
        left.add(comp, BorderLayout.SOUTH);
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        insertIntoNode(comp, getRoot()); // Los componentes añadidos directamente serán añadidos como nodos del primer nivel
    }

    /**
     * Inserta un nodo en el primer nivel de la jerarquía.
     *
     * @param node el nodo a insertar
     */
    public void insertNode(MutableTreeNode node) {
        insertNodeInto(node, getRoot());
    }

    /**
     * Inserta un componente como hijo de otro que ya se encuentra en el árbol.
     * <p>
     * Si se dispone del nodo del padre es preferible usar
     * {@link #insertIntoNode} ya que se evita la búsqueda del nodo en toda la
     * jerarquía.
     *
     * @param comp el componente a insertar
     * @param parent el componente al que se le insertará como hijo
     */
    public void insertInto(Component comp, Component parent) {
        insertIntoNode(comp, getNode(parent));
    }

    /**
     * Inserta un componente como hijo de un nodo que ya está en el árbol.
     *
     * @param comp el componente a insertar
     * @param parent el nodo al que se le insertará como hijo
     */
    public void insertIntoNode(Component comp, MutableTreeNode parent) {
        if (comp instanceof MutableTreeNode) { // Si ya es un nodo
            insertNodeInto((MutableTreeNode) comp, parent);
        } else if (comp instanceof TreeNodePanel) { // Si tiene un nodo
            insertNodeInto(((TreeNodePanel) comp).getNode(), parent);
        } else { // Si no se crea un nuevo nodo
            insertNodeInto(new Node(comp), parent);
        }
    }

    /**
     * Inserta un nodo como hijo de otro que ya se encuentra en el árbol.
     *
     * @param node el nodo a insertar
     * @param parent el nodo al que se le insertará como hijo
     */
    public void insertNodeInto(MutableTreeNode node, MutableTreeNode parent) {
        model.insertNodeInto(node, parent, parent.getChildCount());
    }

    /**
     * Obtiene todos los componentes que forman parte del árbol.
     * <p>
     * Es más conveniente que {@link #getComponents()} ya que evita crear un
     * array extra.
     *
     * @return los componentes que forman parte del árbol
     */
    public Collection<Component> components() {
        ArrayList<Component> list = new ArrayList();
        getComponents(getRoot(), list);
        return list;
    }

    @Override
    public Component[] getComponents() {
        return components().toArray(new Component[0]); // Es más eficiente pasándole un array vacío
    }

    /**
     * Elimina el nodo de este árbol.
     *
     * @param node el nodo a eliminar
     */
    public void remove(MutableTreeNode node) {
        model.removeNodeFromParent(node);
    }

    @Override
    public void remove(Component comp) {
        if (comp == null) {
            return;
        }
        // Hay que buscar el nodo al que pertenece para borrarlo
        MutableTreeNode node = getNode(comp);
        if (node == null || node.equals(getRoot())) {
            return; // Si no se ha encontrado o se intenta borrar la raíz no se hace nada
        }
        remove(node);
    }

    /**
     * Elimina el componente especificado por el índice.
     * <p>
     * Este índice se refiere únicamente a los componentes que se encuentran en
     * el primer nivel, por lo que para borrar niveles intermedios debe usarse
     * otro método.
     */
    @Override
    public void remove(int index) {
        remove((MutableTreeNode) model.getChild(getRoot(), index));
    }

    @Override
    public void removeAll() {
        Enumeration i = getRoot().children();
        while (i.hasMoreElements()) {
            MutableTreeNode child = (MutableTreeNode) i.nextElement();
            model.removeNodeFromParent(child);
        }
    }

    /**
     * Obtiene el nodo en el que se encuentra este componente.
     * <p>
     * Si el componente se encuentra en más de un nodo se devolverá el primero
     * que se encuentre.
     *
     * @param comp el componente
     * @return el nodo encontrado, <code>null</code> si no se encontró
     */
    public MutableTreeNode getNode(Component comp) {
        if (comp instanceof MutableTreeNode) {
            return (MutableTreeNode) comp;
        }
        if (comp instanceof TreeNodePanel) {
            return ((TreeNodePanel) comp).getNode();
        }
        return findNode(getRoot(), comp);
    }

    /**
     * Obtiene todos los nodos del árbol.
     *
     * @return todos los nodos
     */
    public Collection<MutableTreeNode> getNodes() {
        return getRoot().getDescendants();
    }

    /**
     * Busca el nodo al que pertenece un componente a partir de un nodo origen.
     */
    private MutableTreeNode findNode(MutableTreeNode node, Component comp) {
        Enumeration i = node.children();
        while (i.hasMoreElements()) { // Recorre los hijos del nodo
            MutableTreeNode child = (MutableTreeNode) i.nextElement();
            Object userObject = null;
            if (child instanceof DefaultMutableTreeNode) { // Compatible con ambos tipos de nodo
                DefaultMutableTreeNode ch = (DefaultMutableTreeNode) child;
                userObject = ch.getUserObject();
            } else if (child instanceof TreeNode) {
                TreeNode tn = (TreeNode) child;
                userObject = tn.getUserObject();
            }
            if (userObject != null && userObject instanceof Component && ((Component) userObject).equals(comp)) {
                return child; // Se ha encontrado en este nodo
            }
            MutableTreeNode ret = findNode(child, comp); // Se busca recursivamente
            if (ret != null) { // Si se encontró en esta rama se devuelve
                return ret;
            } // Si no se sigue buscando en las demás ramas
        }
        return null; // No se encontró en ninguna rama
    }

    /**
     * Obtiene recursivamente todos los componentes a partir de un nodo
     */
    private void getComponents(MutableTreeNode node, List<Component> list) {
        Enumeration i = node.children();
        while (i.hasMoreElements()) {
            MutableTreeNode child = (MutableTreeNode) i.nextElement();
            Object userObject;
            if (child instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode ch = (DefaultMutableTreeNode) child;
                userObject = ch.getUserObject();
            } else if (child instanceof TreeNode) {
                TreeNode tn = (TreeNode) child;
                userObject = tn.getUserObject();
            } else { // No es un nodo válido
                return;
            }
            if (userObject != null && userObject instanceof Component) { // Si el contenido es un componente
                Component comp = (Component) userObject;
                list.add(comp); // Se añade
            }
            getComponents(child, list); // Se sigue recursivamente por los hijos
        }
    }

    /**
     * Limita el número de nodos visibles al máximo establecido.
     */
    private void boundVisibleRowCount() {
        tree.setVisibleRowCount(Math.max(0, (Math.min(tree.getRowCount(), getMaximumVisibleRows()))));
    }

    /**
     * Si esta GUI está en una ventana, la avisa para que se reajuste.
     */
    private void pack() {
        Window window = (Window) SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.pack();

        }
    }

    /**
     * Define nodos que pueden usarse para construir un {@link TreeViewPanel}.
     * <p>
     * Su propósito es proporcionar una interfaz común compatible con esta
     * clase, ya que a {@link MutableTreeNode} le falta definir
     * {@link #getUserObject}.
     */
    public interface TreeNode extends MutableTreeNode {

        /**
         * Obtiene el contenido del nodo.
         *
         * @return el contenido
         */
        public Object getUserObject();

    }

    /**
     * Clase que se ofrece como opción principal para ser usada como nodo en un
     * {@link TreeViewPanel}.
     * <p>
     * Además de toda la funcionalidad otorgada por
     * {@link DefaultMutableTreeNode}, se encarga de automáticamente mostrar y
     * actualizar su nombre en la jerarquía de nodos a partir del nombre del
     * componente que contiene, así como de avisar al {@link TreeViewPanel} que
     * lo contenga cuando se le inserten o eliminen nodos para que se actualice
     * la GUI; de este modo se libera al programador de esta tarea y le permite
     * trabajar de forma más transparente y sencilla.
     */
    public static class Node extends DefaultMutableTreeNode implements TreeNode {

        private PropertyChangeListener listener;

        /**
         * Crea un nodo vacío.
         */
        public Node() {
            super();
            listener = new PropertyChangeListener() { // Enlaza el nombre del componente con el del nodo
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    javax.swing.tree.TreeNode root = getRoot();
                    if (root instanceof Root) { // Si está en un TreeViewPanel se le avisa
                        ((Root) root).getModel().nodeChanged(Node.this);
                    }
                }
            };
        }

        /**
         * Crea un nodo con el componente como contenido.
         *
         * @param comp el componente
         */
        public Node(Component comp) {
            this();
            setUserObject(comp);
        }

        @Override
        public void setUserObject(Object userObject) {
            if (getUserObject() != null && getUserObject() instanceof Component) { // Si ya tenía un componente se le elimina el listener
                ((Component) getUserObject()).removePropertyChangeListener("name", listener);
            }
            super.setUserObject(userObject); // Elimina el contenido anterior
            if (userObject instanceof Component) { // Si el contenido es un componente se le añade un listener para enlazar el nombre
                ((Component) userObject).addPropertyChangeListener("name", listener);
            }
            javax.swing.tree.TreeNode root = getRoot();
            if (root instanceof Root) { // Si está en un TreeViewPanel se le avisa del cambio
                DefaultTreeModel model = ((Root) root).getModel();
                if (model != null) {
                    model.nodeChanged(Node.this);
                }
            }
        }

        @Override
        public String toString() {
            Object object = getUserObject();
            if (object instanceof Component) { // Si es un componente se usa su nombre
                return ((Component) object).getName();
            } else {
                return super.toString();
            }
        }

        @Override
        public void add(MutableTreeNode newChild) {
            javax.swing.tree.TreeNode root = getRoot();
            if (root instanceof Root) { // Si está en un TreeViewPanel
                // Se añade a través de él para que tenga constancia
                ((Root) root).getTreeViewPanel().insertNodeInto(newChild, this);
            } else { // Si no se añade normalmente
                super.add(newChild);
            }
        }

        /**
         * Elimina el nodo hijo especificado por el índice de este nodo.
         * <p>
         * Es preferible usar este método frente a {@link #remove(int)} ya que
         * este método se encarga de avisar de los cambios al modelo
         * automáticamente.
         *
         * @param childIndex el índice
         */
        public void removeChild(int childIndex) {
            MutableTreeNode node = (MutableTreeNode) getChildAt(childIndex);
            node.removeFromParent();
        }

        @Override
        public void removeFromParent() {
            javax.swing.tree.TreeNode root = getRoot();
            if (root instanceof Root) {
                ((Root) root).getModel().removeNodeFromParent(this);
            } else {
                super.removeFromParent();
            }
        }

        /**
         * Obtiene los hijos de este nodo.
         *
         * @return los hijos del nodo
         */
        public Collection<MutableTreeNode> getChildren() {
            ArrayList<MutableTreeNode> list = new ArrayList<>(getChildCount());
            for (int i = 0; i < getChildCount(); i++) {
                list.add((MutableTreeNode) getChildAt(i));
            }
            return list;
        }

        /**
         * Obtiene todos los descendientes de este nodo.
         *
         * @return los descendientes del nodo
         */
        public Collection<MutableTreeNode> getDescendants() {
            ArrayList<MutableTreeNode> list = new ArrayList<>();
            getDescendants(this, list);
            return list;
        }

        /**
         * Obtiene recursivamente los descendientes de un nodo.
         */
        private void getDescendants(MutableTreeNode node, Collection<MutableTreeNode> list) {
            Enumeration i = node.children();
            while (i.hasMoreElements()) {
                MutableTreeNode child = (MutableTreeNode) i.nextElement();
                list.add(child);
                getDescendants(child, list);
            }
        }

    }

    /**
     * Representa la raíz de una jerarquía de nodos en un {@link TreeViewPanel}.
     * <p>
     * Esta clase únicamente es creada y usada internamente y su propósito es
     * conseguir un vínculo desde la jerarquía de nodos al {@link TreeViewPanel}
     * que los contiene.
     */
    private class Root extends Node {

        /**
         * Crea la raíz con un contenido vacío.
         */
        private Root() {
            super(new JPanel());
        }

        private DefaultTreeModel getModel() {
            return model;
        }

        private TreeViewPanel getTreeViewPanel() {
            return TreeViewPanel.this;
        }

    }
}

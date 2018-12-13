package GUI;

import Container.Storage;
import Container.StorageSelect;
import Geometry.Figure;
import Geometry.Group;
import Observer.MyObserver;
import Observer.Subject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

/* может стоить переписать, где будут различные методы. Т.е. при удалении уведомление про удаление такого-то объекта?????
Так же логично??? ХМХМММММ
* */

// дерево наблюдает, и за ним могут следить О_О
public class JPanelTree extends Subject implements MyObserver {
    private JPanel layer;
    private JTree tree;
    private TreeNode root;
    private DefaultTreeModel model;
    private int[] masSelect = new int[0];
    private DefaultMutableTreeNode curSelect; // переменная для щелчка мыши

    private boolean isAdd; // счетчик нажатий, если четно но убрать выделение, иначе сделать

    JPanelTree(){
        root = create();
        model =  new DefaultTreeModel(root);


        tree = new JTree(model);
        tree.setRootVisible(false);
        //JScrollPane scrollPane = new JScrollPane(tree,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                // пройтись по всему дереву и записать новый массив выделенных элементов, а потом как-то сообщить про это хранилищу выделенных

                    DefaultMutableTreeNode kid = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent(); // последний на который нажали
                int ind = model.getIndexOfChild(root, kid);
                if(masSelect.length == 0)
                    isAdd = true;
                else {
                    boolean pos = contin(ind);
                    if (pos)
                        isAdd = false;
                    else
                        isAdd = true;
                }


                curSelect = kid; // потом убрать это! или нет хех.
                notifySelect(); // говорим шпионам, что мы выделены
            }
        });


        layer = new JPanel();
        tree.setSelectionRow(0);
        layer.add(tree);

    }

    private boolean contin(int ind){

        for(int i = 0; i<masSelect.length; i++)
            if(ind == masSelect[i])
                return true;
        return false;
    }

    private TreeNode create(){
        DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode("Root");
        return mainNode;
    }

    public Figure getSelect(){
        return (Figure) curSelect.getUserObject();
    }

    public boolean isAdd(){
        return isAdd;
    }

    public void add(DefaultMutableTreeNode newNode){

        //tree.setSelectionRow(0); // чтобы добавлялось отдельно
        //DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree
            //    .getLastSelectedPathComponent(); // получаем последний добавленный лист

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) model.getRoot();
        model.insertNodeInto(newNode, selectedNode, selectedNode
                .getChildCount());


       // model.reload(); // обновляем

        // now display new node, прокручиваем до туда
       /* TreeNode[] nodes = model.getPathToRoot(newNode);
        TreePath path = new TreePath(nodes);
        tree.scrollPathToVisible(path);*/
        tree.setSelectionRows(masSelect);
    }


    public void delete(int pos){
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) model.getChild(model.getRoot(), pos);
        //tree.setSelectionRow(1+pos); // плюс корень если что можно заменить на  model.getChild(model.getRoot(), pos);
        //DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree
        //        .getLastSelectedPathComponent(); // получаем последний добавленный корень
        if (selectedNode.getParent() != null)
            model.removeNodeFromParent(selectedNode); // обновляет сам
    }

    public void edit() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree
                .getLastSelectedPathComponent(); // получаем последний добавленный корень
            String newText;
            if ((newText = JOptionPane.showInputDialog(layer, "Edit new text",
                    selectedNode.toString())) != null) {
                selectedNode.setUserObject(newText);
                model.nodeChanged(selectedNode);
            }
    }

    public JTree getTree() {
        return tree;
    }

    public void clear(){
        int size = root.getChildCount();
        for(int i = 1; i<size;i++)
            delete(1);
        delete(0);
    }

    private void build(Storage<Figure> stor){
        if(root.getChildCount() != 0)
            clear();

        while (stor.hasNext()) {
            Figure f = stor.next();
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(f);
            add(node);
            if(f instanceof Group)
                creatersGroup((Group) f, node);
        }
    }

    private void creatersGroup(Group f, DefaultMutableTreeNode parent){
        Figure[] mas = f.getData();

        for(int i = 0; i<f.getCount(); i++) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(mas[i]); // kid

            if(mas[i] instanceof Group)
                creatersGroup((Group) mas[i], newNode);
            goo(parent, newNode);
        }

        model.reload();
        //tree.setSelectionRows(masSelect);
    }

    private void goo(DefaultMutableTreeNode parent, DefaultMutableTreeNode child){
        model.insertNodeInto(child, parent, parent.getChildCount());
    }

    @Override
    public void onSubjectChanged(Subject sub){
       build((Storage<Figure>) sub);
       model.reload();
       tree.setSelectionRows(masSelect); // чтобы не снимались, когда мы добавляепм новую фигуру, заново подсвечиваем
    }

    public int find(Figure x){
        DefaultMutableTreeNode a1 = new DefaultMutableTreeNode(x);
        Enumeration<DefaultMutableTreeNode> kids = root.children();

        int size = root.getChildCount();
        if(!kids.hasMoreElements())
            return -1;

        int ind = 0;

        // ищем объект, может в отдельныю функцию вынести?
        while(size!=0){
            DefaultMutableTreeNode p = kids.nextElement();
            if(p.getUserObject()==a1.getUserObject())
                break;
            ind++;
            size--;
        }

        return ind; // индекс в корне
    }

    @Override
    public void setSelect(Subject who) {

        StorageSelect<Figure> storage = (StorageSelect<Figure>) who;
        masSelect = new int[storage.size()];
        int i = 0;

        while(storage.hasNext()) {
            Figure f = storage.next();
            int ind = find(f);
            if(ind==-1)
                break;

            masSelect[i] = ind;
            i++;
        }


        tree.setSelectionRows(masSelect);
    }

    @Override
    public void stickyMove(Subject who, int dx, int dy) {

    }

}
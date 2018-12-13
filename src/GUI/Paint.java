package GUI;

import Commands.Command;
import Commands.CopyCommand;
import Commands.PasteCommand;
import Container.CommandHistory;
import Container.Storage;
import Container.StorageCross;
import Container.StorageSelect;
import Factory.MyFactory;
import Geometry.*;
import Geometry.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class Paint extends JFrame{

    private JButton crtTrian,crtCirc, crtRect, crtLine, changeCol, sizePlus, sizeMinus, delete, deleteAll, group, ungroup;
    private JCheckBox drag;
    private JFrame frame = this;
    private JPanel mainPanel, paintPanel, tools, layers;
    private JPanelTree tree;
    private JMenuBar menu;
    private JTextField color;
    private JMenuItem save, load;

    private static Figure current = null; // текущая выделенная фигура
    private Figure copy;
    private Storage<Figure> storage;
    private StorageSelect<Figure> listIsDown;
    private Class[] list = {Triangle.class, Circle.class, Rectangle.class, Line.class};
    private int choice=1; // по умолчанию круг
   // private ArrayList<Figure> listIsDown;
    private CommandHistory history;

    private boolean ctrl = false;
    private boolean keyC = false;
    private boolean keyV = false;
    private boolean keyZ = false;

    private int copyX1, copyY1; // переменная нужна для передвижения мышью, сюда записываем, куда была нажата мышка

    public static final int weightPaint = 700;
    public static final int heightPaint = 700;

    private Color colorDefault = Color.GREEN; // по умолчанию цвет зеленый, сделать так чтобы можно было изменить глобально

    public Paint(){
        super("QWERTY");
        setSize(900,900);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        storage = new Storage();
        //listIsDown = new Storage<Figure>(); // хранилище выделенных фигур
        listIsDown = new StorageSelect<Figure>();

        // 3 панели, главная, для рисования и для инструментов
        mainPanel = new JPanel(new BorderLayout());
        paintPanel =new JPanel(null);
        //tools = new JPanel(new GridLayout(12, 1, 20, 20));
        tools = new JPanel(new GridLayout(1, 12, 20, 20));
        layers = new JPanel(); // панель, предназначенная тольк для дерева

        paintPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        paintPanel.setPreferredSize(new Dimension(700,700));
        layers.setPreferredSize(new Dimension(200, 700));


        // создаем кнопки
        crtTrian = new JButton("Triangle");
        crtRect = new JButton("Rectangle");
        crtCirc = new JButton("Circle");
        crtLine = new JButton("Line");
        changeCol = new JButton("Color");

        color = new JTextField(); // для ведения цвета
        color.setFont(new Font(Font.SERIF, Font.PLAIN, 18));

        // увеличение размера фигуры
        sizePlus = new JButton("+");
        sizeMinus = new JButton("- ");

        group = new JButton("Grouping");
        ungroup = new JButton("Ungroup");
        drag = new JCheckBox("Drag"); // кнопка для создание липкого объекта
        delete = new JButton("Delete");
        deleteAll = new JButton("Delete all");


        // складываем кнопки на панель
        tools.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tools.add(crtTrian); tools.add(crtRect); tools.add(crtCirc);
        tools.add(crtLine);  tools.add(changeCol); tools.add(color);
        tools.add(sizePlus); tools.add(sizeMinus);
        tools.add(group); tools.add(ungroup); tools.add(drag);
        tools.add(delete); tools.add(deleteAll);

        menu = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu help = new JMenu("How is it working?"); // инструкция
        JMenu setting = new JMenu("Setting");
        JMenuItem create = new JMenuItem("Create"); // спрашивать сохранить ли текущий рисунок, сохранять, и создавать новый проект
        save = new JMenuItem("Save"); // сохранять рисунок туда, куда надо
        load = new JMenuItem("Open"); // открывать нужный рисунок
        file.add(create);
        file.add(save);
        file.add(load);
        menu.add(file);
        menu.add(help);
        menu.add(setting); // сделать для файлов потом! НЕ ЗАБЫТЬ!!!!!


        // дерево

        JButton editList = new JButton("EDIT LIST"); // проверим, как меняется имя, потом перебросить на правую кнопку МЫШИ
        tree = new JPanelTree();
        layers.add(editList);
        layers.add(tree.getTree());

        editList.addActionListener((e) -> {
            tree.edit();
        });
        storage.addObserver(tree); // следим за хранилищем (О_О)
        listIsDown.addObserver(tree);
        tree.addObserver(listIsDown); // двухсторонняя слежка
        //нужно ли дереву следить за хранилищем основным, а вдруг мы из дерева что-то удалим. оно же должно удалиться и в выделенных объектаъ, и освновной


        mainPanel.add(paintPanel, BorderLayout.CENTER);
        mainPanel.add(layers, BorderLayout.WEST);
        mainPanel.add(tools, BorderLayout.NORTH);
        frame.setJMenuBar(menu);

       // listIsDown = new ArrayList<>();
        history = new CommandHistory();

        drag.addItemListener((e) -> {
            if(current==null)
                return;

            if(drag.isSelected())
                current.doSticky();
            else
                current.doNotSticky();
            paintPanel.requestFocusInWindow();

        });

        // обработчики нажатия кнопок
        crtTrian.addActionListener((e) -> { choice = 0; paintPanel.requestFocusInWindow(); });
        crtCirc.addActionListener((e)-> { choice = 1; paintPanel.requestFocusInWindow(); });
        crtRect.addActionListener((e) -> { choice = 2; paintPanel.requestFocusInWindow(); });
        crtLine.addActionListener((e) -> { choice = 3; paintPanel.requestFocusInWindow(); });

        sizePlus.addActionListener((e) ->  { changeScale(1); paintPanel.requestFocusInWindow(); });
        sizeMinus.addActionListener((e) -> { changeScale(2); paintPanel.requestFocusInWindow(); });

        changeCol.addActionListener((e) -> {
                    String s = color.getText();
                    String[] mas = s.split(" ");
                    int[] rgb = new int[3];
                    for(int i = 0 ;i<3; i++)
                        rgb[i] = Integer.valueOf(mas[i]);

                    Color col = new Color(rgb[0], rgb[1], rgb[2]);
                    newColor(col);

                    paintPanel.requestFocusInWindow();
        });

        group.addActionListener((e) -> { group(getGraphics()); paintPanel.requestFocusInWindow(); });
        ungroup.addActionListener((e) -> { ungroupElement(); paintPanel.requestFocusInWindow(); });
        delete.addActionListener((e) -> { deleteFigure(); paintPanel.requestFocusInWindow(); });
        deleteAll.addActionListener((e) -> { clearAll(); paintPanel.requestFocusInWindow(); });

        // обработка нажатия на пункт меню - сохранить
        save.addActionListener((e) -> {
            JFileChooser fileChooser = new JFileChooser(new File( System.getProperty("user.dir")));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showSaveDialog(Paint.this);

            // что-то пошло не так
            if (result != JFileChooser.APPROVE_OPTION )
                return;

            JOptionPane.showMessageDialog(Paint.this,
                    "Файл '" + fileChooser.getSelectedFile() +
                            " ) сохранен");
            storage.save(fileChooser.getSelectedFile());

        });

        load.addActionListener((e) ->{
            if(!storage.isEmpty())
                return;

            JFileChooser fileChooser = new JFileChooser(new File( System.getProperty("user.dir")));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(Paint.this);

            // Если выбор файла не прошел успешно, то уходит
            if (result != JFileChooser.APPROVE_OPTION )
                return;

            MyFactory factory = new MyFactory();
            storage.load(paintPanel.getGraphics(), factory, fileChooser.getSelectedFile());

            while (storage.hasNext()) {
                Figure f = storage.next();
                addObserbers(f); // работает ли?
                if(f.isPutDown())
                    listIsDown.add(f);
                f.draw();
            }

            paintPanel.requestFocusInWindow();
        });

        paintPanel.addMouseListener(new MouseAdapter() {
            int x, y, xCopy, yCopy;

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                x = mouseEvent.getX();
                y = mouseEvent.getY();

                xCopy = x; yCopy = y;

                copyX1 = x; copyY1 = y;

                boolean flag = false; // флаг нажатие ли на фигуру
                try {
                    while (storage.hasNext()){
                        Figure f = storage.next(); // получаем объект

                        // если нажали по фигуре
                        if(f.isInside(x,y)){
                            flag = true;
                            boolean isDown = listIsDown.contains(f); // нажата ли фигура, есть ли она в очереди

                            if(isDown) {
                                listIsDown.delete(f);
                                //listIsDown.remove(f); // сделать ее не нажатой, если была нажата
                                f.setColor(Color.GREEN);
                            } else {
                                listIsDown.add(f); // иначе добавить ее в нажатый список
                                f.setColor(Color.BLUE);
                            }

                            // как-то по другому
                           // if(current!=null)
                           //     setNotDrag(); // говорим, что теперь последний объект больше не липкий
                            current = (listIsDown.size() == 0) ? null : listIsDown.get(listIsDown.size()-1); // последний нажатый станет текущим

                           // System.out.println(current);
                           /* if(current.isSticky()) {
                                setDrag();
                                //System.out.println("F");
                            }*/


                            f.draw(); // отрисовать изменненый
                        }
                    }

                    // не создаем новый объект, так как было произведено нажатие по фигуре
                    if(flag)
                        return;

                    Figure c = (Figure) list[choice].getDeclaredConstructor(int.class, int.class, Graphics.class, Color.class).
                            newInstance(x, y, paintPanel.getGraphics(), colorDefault);

                    //tree.add(c);  // добавялем в дерево фигуру


                    if(c.setPlace(c.getCorX(),c.getCorY()))
                        c.draw();
                    else
                        return;

                    addObserbers(c); // присваиваем фигуры наблюдателей и говорим ей шпионить за всеми
                    storage.add(c);
                    paintPanel.requestFocusInWindow();

                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                // который хотим перетащить надо выделить
                paintPanel.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent mouseEvent) {
                        if(current == null)
                            return;

                        int dx = mouseEvent.getX() - xCopy;
                        int dy = mouseEvent.getY() - yCopy;
                        xCopy = mouseEvent.getX();
                        yCopy = mouseEvent.getY();

                        changeLocation(dx,dy);
                    }
                });
            }
        });

        paintPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {

                if(keyEvent.getKeyCode() == KeyEvent.VK_CONTROL){
                    ctrl = true;
                } else if (keyEvent.getKeyCode() == KeyEvent.VK_C){
                    keyC = true;
                } else if(keyEvent.getKeyCode() == KeyEvent.VK_V) {
                    keyV = true;
                } else if(keyEvent.getKeyCode() == KeyEvent.VK_Z) {
                    keyZ = true;
                }

                else if(keyEvent.getKeyCode() == KeyEvent.VK_DELETE){
                    clearAll();
                    return;
                } else if(keyEvent.getKeyCode() == 67){
                    choice = 1;
                } else if(keyEvent.getKeyCode() == 82){
                    choice = 2;
                } else if(keyEvent.getKeyCode() == 76){
                    choice = 3;
                } else if(keyEvent.getKeyCode() == 84){
                    choice = 0;
                }

                if(current==null)
                    return;
                else if(keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    deleteFigure();
                }else if(keyEvent.getKeyCode() > 36 && keyEvent.getKeyCode() < 41){
                    int dx = 0, dy = 0;
                    switch (keyEvent.getKeyCode()) {
                        case 37: // left
                            dx = -5;
                            break;
                        case 38: // bottom
                            dy = -5;
                            break;
                        case 39: // right
                            dx = 5;
                            break;
                        case 40: // up
                            dy = 5;
                            break;
                    }

                    // для перерисовки
                    changeLocation(dx, dy);

                } else if(keyEvent.getKeyCode() == 107)  // +
                    changeScale(1);
                else if(keyEvent.getKeyCode() == 109)  // -
                    changeScale(2);


                if(ctrl && keyC) {
                    executeCommand(new CopyCommand(Paint.this));
                } else if(ctrl && keyV){
                    executeCommand(new PasteCommand(Paint.this));
                } else  if(ctrl && keyZ){
                    undo();
                }
            }
        });

        paintPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.VK_CONTROL){
                    ctrl = false;
                } else if (keyEvent.getKeyCode() == KeyEvent.VK_C){
                    keyC = false;
                } else if(keyEvent.getKeyCode() == KeyEvent.VK_V) {
                    keyV = false;
                } else if(keyEvent.getKeyCode() == KeyEvent.VK_Z) {
                    keyZ = false;
                }
            }
        });

        add(mainPanel);
        paintPanel.requestFocusInWindow();
        setVisible(true);

        pack();
    }

    // меняем местоположение
    // у группы для каждой фигуры отдельное перечение
    private void changeLocation(int dx, int dy){
        if(!findCross(dx, dy))
            return;
        /*if(current.hasOberver()){
            current.deleteObserver();
        }

        int dxCopy = dx;
        int dyCopy = dy;


        while (storage.hasNext()) {
            Figure f = storage.next();
             if (current!= f && current.isCross(f)) { // если фигуры пересекаются
               // current.addObserver(f); // фигура, которая с ним пересекается теперь следит за ним

                 if(current.isSticky() || f.isSticky()){

                    if(f.isSticky()){
                        // если обычная или липкая приклеилась к липкой
                        current = f;
                        changeLocation(dx, dy);
                        storage.begin();
                        return;

                    }
                    // и если эта липкая фигура, то те с кем она пересекается мы проверяем на новое местоположение
                   *//* else if(f instanceof  Group){
                         if(!f.setPlace(dx, dy)) {
                            storage.begin();
                            return;
                        }
                    }*//*
                    else if(!f.setPlace(f.getCorX()+dxCopy, f.getCorY()+dyCopy)) {
                        storage.begin();
                        return; // мы не можем двигать
                    }
                }
                //current.addObserver(f); // фигура, которая с ним пересекается теперь следит за ним
            }
        }*/

        current.move(dx, dy);


        // если мы уже наблюдали, то очистить шпионов и приготовить новую партию
      /*  if(current.hasOberver()){
            current.deleteObserver();
        }
        StorageCross cross = null;
        cross = new StorageCross();

        // ArrayList<Figure> cross = new ArrayList<>();
        if(current instanceof Group) {
            // надо пройтись по группе и запустить проверку
            while (storage.hasNext()) {
                // cross = new StorageCross();
                Figure tmp = storage.next();

                // если фигура пересекается
                boolean flag = ((Group) current).findCrossWith(tmp); // отправляем в группу фигуру и просим вернуть, если есть фигуру, с которой tmp пересекается
                if (flag) {
                    cross.add(tmp);
                    //System.out.println(cross.size());
                    // cross.add(x); // нужно ли очищать после отрисовки
                   // current.addObserver(cross);
                }
            }
            if(!cross.isEmpty()) {
                current.addObserver(cross);
                current.setStorageCross(cross);
            }

        }else{
            //cross = new StorageCross();
            findCross(cross, current);
            current.addObserver(cross);
        }

        current.move(dx, dy);

        for(int i = 0; i<cross.size(); i++){
            cross.get(i).draw();
        }*/
        //for(Figure x :cross)
        //    x.draw();
    }



    // удаляем фигуру
    private void deleteFigure(){
        if(current == null)
            return;
        //tree.delete(storage.find(current)); // удаляем ее из дерева, надо передать ее позицию в дереве, а это будет номер по хранилищу

        setNotDrag();
        delComponent(current); // из хранилища удаляем
        //listIsDown.add(current);
        listIsDown.delete(current); // удаляем его из выделенных
        current.delete(); //
        //
        //setNotDrag(); // снимаем галочку
       // current = null; // удаляем ссылку на текущий объект, так как он удален, можно присвоить последнему в списке выделенных
        current = (listIsDown.size()==0) ? null : listIsDown.get(listIsDown.size()-1);
    }

    // устанавливаем новый цвет у выделенной фигуры
    private void newColor(Color c){
        if(current==null)
            return;
        else
            current.setColor(c);
    }

    // ищем фигуры с которыми пересекаемся
    // сделать,чтобы возвращал список
    //private void findCross(StorageCross cross, Figure obj){
    private boolean findCross(int dx, int dy){

        if(current.hasOberver()){
            current.deleteObserver();
        }

        int dxCopy = dx;
        int dyCopy = dy;


        while (storage.hasNext()) {
            Figure f = storage.next();
            if (current!= f && current.isCross(f)) { // если фигуры пересекаются
                // current.addObserver(f); // фигура, которая с ним пересекается теперь следит за ним

                if(current.isSticky() || f.isSticky()){

                    if(f.isSticky()){
                        // если обычная или липкая приклеилась к липкой
                        current = f;
                        changeLocation(dx, dy);
                        storage.begin();
                        return false;

                    }
                    // и если эта липкая фигура, то те с кем она пересекается мы проверяем на новое местоположение
                  /* else if(f instanceof  Group){
                         if(!f.setPlace(dx, dy)) {
                            storage.begin();
                            return;
                        }
                    }*/
                    else if(!f.setPlace(f.getCorX()+dxCopy, f.getCorY()+dyCopy)) {
                        storage.begin();
                        return false; // мы не можем двигать
                    }
                }
                //current.addObserver(f); // фигура, которая с ним пересекается теперь следит за ним
            }
        }
        return true;
        //private void findCross(ArrayList<Figure> cross){

       /* while(storage.hasNext()){

            Figure f = storage.next();
            if(obj!= f && obj.check(f)) { // если фигура с кем-то пересекается, то добавляем ее в список пересеченных
                if(f.isSticky()) {
                    current = f;
                    cross.clear();
                    findCross(cross, f); // пересчитываем и передаем урпавление липкой фигуре
                    // group?
                    return;
                }else {
                    cross.add(f);
                }

            }
        }
        obj.setStorageCross(cross); // нужна ли проверка на пустату*/
        //obj.addObserver(cross);
    }

    // удаляем фигуру из хранилища
    public void delComponent(Figure current){
        int i = 0;
        for(;i<storage.size(); i++)
            if(current==storage.get(i))
                break;

        storage.begin();
        storage.delete(i);
    }

    public void clearAll(){
        //tree.clear();
        listIsDown.clear();
        current = null;
        storage.clear();
        setNotDrag();
        paintPanel.removeAll();
        revalidate();
        repaint();
    }

    // меняем размер последнеей и фигуры. а также отрисовываем те, с которыми пересекаемся
    private void changeScale(int mode){
        if(current == null)
            return;

        StorageCross cross = new StorageCross();
       // ArrayList<Figure> cross = new ArrayList<>();
        if (!findCross(0,0))
                return;
       // findCross(cross,current);
        current.recalculate(mode);

        for(int i = 0; i<cross.size(); i++){
            cross.get(i).draw();
        }
       // for(Figure x: cross)
        //    x.draw();
    }

    // группируем
    // при группировке находим все выделенные, а после создаем их них группу
    // выделенные элементы, удаляем из дерева, и направляем в группу(которая теперь родитель) в дерево
    private void group(Graphics g){
        Group one = new Group(0,0,g);
        //tree.add(one);

        int i = 0;
        for( ; i<storage.size(); i++){
            Figure f =  storage.get(i);
            boolean isDown = listIsDown.contains(f); // проверяем нажата ли фигуры

            if(!isDown) // если фигура не нажата, то не работаем с ней
                continue;
            one.addFigure(f);
            f.setGroup(one); // говорим фигуре, что она в группе, а также группе, что она в группе
            // удалить фигуры нажатые, которые добавляем в список из списка нажатых
           // listIsDown.remove(f);
            listIsDown.delete(f);

            //
            // tree.createGroup(i);
            //tree.delete(i); // удаляем просто фигуру из деревав
            //tree.addGroup(f);

            storage.delete(i); // удаляем его из хранилища
            i--; // так как удалили из хранилища
        }

        storage.add(one); // пихаем группу в хранилище
        listIsDown.add(one); // группа у нас нажатая
        current = one;
        //addObserbers(one);

    }

    // разгруппировываем элементы
    private void ungroupElement(){
        for(int i = 0; i< listIsDown.size(); i++){
            if (listIsDown.get(i) instanceof Group) {
                Group x = (Group) listIsDown.get(i);
                //tree.ungroup(x);
                int size = x.getCount();
                Figure[] mas = x.ungroup(storage);
                for(int k = 0; k<size; k++)
                    makePutDown(mas[k]);

                storage.delete(x); // удаляем группу из хранилища
                //listIsDown.remove(i); // либо по индексу можно, что быстрее
                //listIsDown.delete(i);
                listIsDown.delete(x);

                if(current == x)  // если до этого текущим элементом была удаляемая групппа, то изменяем указатель
                    current = listIsDown.get(0);

                i--; // без этого все элементы становятся по одному
                break; // поэтому и break;
            }
        }
    }

    // делаем фигуру выделенной
    public void makePutDown(Figure f){
        listIsDown.add(f);
    }

    private void executeCommand(Command com){
        if(com.execute())
            history.push(com);
    }

    // отмена действия, сделать такое же когда удаляешь объект
    // починить
    private void undo(){
        if(history.isEmpty())
            return;

        Command com = history.pop();

        // надо удалить ее из хранилища
        if(com!=null)
            com.undo();
    }

    public void setCopyFigure(){
        try {
            copy = current.getClass().getDeclaredConstructor(Figure.class).newInstance(current);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public Figure getCopyFigure(){
        return copy;
    }

    // вставляем в панель
    public void insertToPanel(){
        // получаем координаты, где сейчас мышь
        // чтобы не вышли за границы
        Point location = paintPanel.getMousePosition();
        if(location == null)
            return;

        int x = ((int)location.getX());
        int y = ((int)location.getY());

        Figure c = null;
        try {
            c =  copy.getClass().getDeclaredConstructor(Figure.class).newInstance(copy);
        } catch (NoSuchMethodException | IllegalAccessException  | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        c.setCorX(x);
        c.setCorY(y);
        c.draw();
        storage.add(c); // добавляем в хранилище, но не выделяем ее

        // теперь фигура, которую мы копируем будет фигура, которая уже была вставлена. Фгуры идентичны и легче запихнуть их
        // в очередь, иначе придется передавать все хранилище и отрисовывать, а это долго
        copy = c;
    }

    private void addObserbers(Figure f){
       /* while (storage.hasNext()){
            Figure x = storage.next();
            f.addObserver(x);
            x.addObserver(f);
        }*/
    }

    private void setDrag(){
        drag.setSelected(true);
     //   current.doSticky();
    }

    private void setNotDrag(){
        drag.setSelected(false);
      //  current.doNotSticky();
    }
}
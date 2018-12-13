package Observer;

public interface MyObserver {
    /* Дерево наблюдает за хранилещем: 1 метод, когда в него что-то добавляется, а 2 когда фигура попадает в выделенный список
    Хранилище наблюдает за дерево: 1 метод вызывается, когда мы удаляем из дерева что-то, оно и в хранилище удалится
    (прописать этот функционал), 2 метод когда на узел дерева нажимают мышкой, мы уведомляем хранилище выделенных объектов
    * */

    //  ПЕРЕПИСАТЬ!
     void onSubjectChanged(Subject who); // объект изменился
     void setSelect(Subject who); // объект выделился
     void stickyMove(Subject who, int dx, int dy);
     //void findCross(Subject who);

   /* // SUBJECT - FIGURE!!!
    void addSubject(Subject fig);
    void deleteSubject(Subject fig);

    void setSelect(Subject fig);*/
}
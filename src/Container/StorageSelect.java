package Container;

import GUI.JPanelTree;
import Geometry.Figure;
import Observer.Subject;

import java.awt.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

// может сделать абстрактный класс контейнер и он наследуется от объекта, а тут уже наследование от контейнера
public class StorageSelect<T> extends Subject implements Observer.MyObserver{
    private ArrayList<T> mas;
    private int currentIndex = -1;
    //указатель начинается с 0. Если -1, то он никуда не указывает

    //конструкторы
    public StorageSelect(){
        mas = new ArrayList<>();
    }

    public StorageSelect(int size){
        mas = new ArrayList<>(size);
    }

    public StorageSelect(StorageSelect s){
        mas = new ArrayList<>(s.mas);
    }

    public int size(){
        return mas.size();
    }

    public void add(T obj){
        mas.add(obj);

        notifySelect();
    }

    public T get(int ind){
        try {
            return mas.get(ind);
            //return mas[ind];
        }catch (IndexOutOfBoundsException e){
            throw new NoSuchElementException();
        }
    }

    public boolean delete(int index){
        try {
            mas.remove(index);
            notifySelect();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean delete(T x){
        boolean a = mas.remove(x);
        notifySelect();
        return a;
    }

    public boolean contains(T x){
        return mas.contains(x);
    }

    public boolean hasNext(){

        if(currentIndex >= mas.size()-1) {
            currentIndex = -1;
            return false;
        }
        return true;
    }

    public T next(){
        try {
            currentIndex = currentIndex + 1;
            return mas.get(currentIndex);
        }catch (IndexOutOfBoundsException e){
            currentIndex = currentIndex - 1;
            throw new NoSuchElementException();
        }
    }

    public void clear(){
        mas.clear();
        notifySelect();
    }

    // методы сработают, когда мы выделим что-то на дереве
    @Override
    public void onSubjectChanged(Subject who) {

    }

    @Override
    public void setSelect(Subject who) {
        // его еще надо отрисавать потом другим цветом
        JPanelTree tree = (JPanelTree) who;

        Figure tmp = tree.getSelect();
        if(tree.isAdd()) {
            add((T) tmp);
            tmp.setColor(Color.BLUE);

        }
        else {
            delete((T) tmp);
            tmp.setColor(Color.GREEN);
        }
        tmp.draw();
        notifySelect();

    }

    @Override
    public void stickyMove(Subject who, int dx, int dy) {

    }
}
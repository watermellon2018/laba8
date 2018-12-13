package Container;

import Geometry.Figure;
import Observer.MyObserver;
import Observer.Subject;

import java.util.NoSuchElementException;

// хранит фигуры, с которым фигура, которая и создала это хранилище, пересекается
public class StorageCross implements MyObserver {
    private int size = 0;
    private Figure mas[];
    private int currentIndex = -1;
    //указатель начинается с 0. Если -1, то он никуда не указывает

    //конструкторы
    public StorageCross(){
        mas = new Figure[2];
    }

    public int size(){
        return size;
    }

    public void add(Figure obj){
        resize();
        mas[size] = obj;
        size = size + 1;
      //  notifyEveryone();
    }

    private void resize(){
        if(size*2>=mas.length){
            //выделяем дополнительно память
            Figure[] arr = new Figure[mas.length*2];
            System.arraycopy(mas,0,arr,0,mas.length); //копируем старый массив в новый or size
            mas = arr; // меняем ссылки и теперь наш основной массив стал больше
        }
    }

    public Figure get(int ind){
        try {
            return mas[ind];
        }catch (IndexOutOfBoundsException e){
            throw new NoSuchElementException();
        }
    }

    //удаление текущего значения
    public boolean delete(){
        try {
            mas[size-1] = null;
            size = size - 1;

            return true;//если удаление прошло успешно
        }catch (Exception e) {
            return false;
        }
    }

    //удаление по индексу
    public boolean delete(int index){
        try {
            mas[index] = null;
            shift(index);//сдвигаем
            size = size - 1;

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void clear(){
        for(int i = 0; i<size; i++)
            delete();
    }

    private void shift(int ind){
        for(int i = ind; i<size-1; i++){
            mas[i] = mas[i+1];
        }
        mas[size-1] = null;
    }

    public boolean isEmpty(){
        if(size==0)
            return true;
        return false;
    }

    @Override
    public void onSubjectChanged(Subject who) {

    }

    @Override
    public void setSelect(Subject who) {

    }


    @Override
    public void stickyMove(Subject who, int dx, int dy) {
        for(int i =0; i<size; i++){
            mas[i].move(dx, dy);
        }
    }
}

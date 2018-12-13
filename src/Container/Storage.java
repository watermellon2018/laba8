package Container;
import Factory.Factory;
import Geometry.Figure;
import Observer.Subject;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Storage<T> extends Subject {
    private int size = 0;
    private T mas[];
    private int currentIndex = -1;
    //указатель начинается с 0. Если -1, то он никуда не указывает

    //конструкторы
    public Storage(){
        mas = (T[]) new Object[2];

    }

    public Storage(int size){
        this.size = size;
        mas = (T[]) new Object[size];
    }

    public Storage(Storage s){
        mas = (T[]) s.mas;
        size = s.size;
    }

    public int size(){
        return size;
    }

    public void add(T obj){
        resize();
        mas[size] = obj;
        size = size + 1;
        notifyEveryone();
        // уведомить шпионов, что теперь и мы за ними следим, и они могут следить за нами
    }

    private void resize(){
        if(size*2>=mas.length){
            //выделяем дополнительно память
            T [] arr= (T[]) new Object [mas.length*2]; // создаем новый массив нужной длины
            System.arraycopy(mas,0,arr,0,mas.length); //копируем старый массив в новый or size
            mas = arr; // меняем ссылки и теперь наш основной массив стал больше
        }
    }

    public T get(int ind){
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
            notifyEveryone();

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
            notifyEveryone();

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean delete(T x){
        for(int i = 0; i < size; i++){
            if(mas[i] == x){
             return delete(i);
            }
        }
        return false; // нет такого элемента
    }

    private void shift(int ind){
        for(int i = ind; i<size-1; i++){
            mas[i] = mas[i+1];
        }
        mas[size-1] = null;
    }

    /*public T getObject(int index){
        try {
            return mas[index];
        }catch (IndexOutOfBoundsException e){
            throw new NoSuchElementException();
        }

    }*/

    public T next(){
        try {
            currentIndex = currentIndex + 1;
            return mas[currentIndex];
        }catch (IndexOutOfBoundsException e){
            currentIndex = currentIndex - 1;
            throw new NoSuchElementException();
        }
    }

    public boolean isEmpty(){
        if(size==0)
            return true;
        return false;
    }

    //проверка есть ли еще элементы в хранилище
    public boolean hasNext(){
        if(currentIndex >= size-1) {
            currentIndex = -1;
            return false;
        }
        return true;
    }

    public boolean contains(T x){
        for(int i = 0; i<size; i++)
            if(mas[i].equals(x))
                return true;
        return false;
    }

    public boolean begin(){
        //ставим в начало
        currentIndex = -1;
        return true;
    }

    public void clear(){
        size = 0;
        currentIndex = -1;
        T [] arr= (T[]) new Object [2];
        mas = arr;
        notifyEveryone();
    }

    public void save(File f){
        try(FileWriter writer = new FileWriter(f, false)){
            writer.write("Count of figures: ");
            writer.write(String.valueOf(size));
            writer.append('\n');

            for(int i = 0; i<size; i++)
                ((Figure) mas[i]).save(writer);

            writer.flush();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void load(Graphics g, Factory factory, File file){
        try {
            FileReader fr = new FileReader(file);
            Scanner scan = new Scanner(fr);
            String s = scan.nextLine();

            // находим количество фигур
            int a = s.lastIndexOf(" ");
            int count = Integer.valueOf(s.substring(a+1, s.length()));

            size = count;
            mas = (T[]) new Object[count*2];

            // пока не загрузим все фигуры
            for(int i = 0; i<count; i++){
                String nameFig = scan.nextLine();
                nameFig = nameFig.substring(0, nameFig.length()-2); // определяем, что это за фигура

                mas[i] = (T) factory.createFigure(nameFig, g);

                if(mas[i]!=null)
                    ((Figure)mas[i]).load(scan,factory);
            }

            fr.close();
            notifyEveryone();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int find(T f){
        for(int i = 0; i<size; i++)
            if(f == mas[i])
                return i;
        return -1; // нету
    }
}
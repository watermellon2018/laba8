package Geometry;

import Container.Storage;
import Container.StorageCross;
import Factory.Factory;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Group extends Figure {

    private int count; // количество элементов в группе
    private Figure[] figInInside; // массив фигур, которые находятся внутри
    private static int numberFig = 1;

    public Group(int a, int b, Graphics g) {
        super(a,b,g);
        count = 0;
        name = "Group"+numberFig;
        numberFig++;
        figInInside = new Figure[4];
    }

    // добавляем фигуру
    public void addFigure(Figure f){
        if(count*2 >= figInInside.length)
            resize();

        figInInside[count] = f;
        count++;
    }

    private void resize(){
        Figure [] arr = new Figure[figInInside.length*2]; // создаем новый массив нужной длины
        System.arraycopy(figInInside,0,arr,0,figInInside.length); //копируем старый массив в новый or size
        figInInside = arr; // меняем ссылки и теперь наш основной массив стал больше
    }

    // отрисовываем фигуры, вызываем метод для каждой
    public void draw(){
        for(int i = 0; i<count; i++)
            figInInside[i].draw();
    }

    public boolean isPutDown(){
        return figInInside[0].isPutDown();
    }

    // удаляем группу
    public void delete(){
        for(int i = 0; i<count; i++) {
            figInInside[i].delete();
            figInInside[i].delGroup();
        }
        count = 0;
    }

    public void save(FileWriter file) throws IOException {
        file.write("Group {");
        file.append('\n');
        for(int i = 0; i<count; i++)
            figInInside[i].save(file);


        file.write("}");
        file.append('\n');

        file.flush();
    }

    public void load(Scanner scan, Factory factory){
        String secondName;
        int size = 0;
        while(!(secondName = scan.nextLine()).equals("}")) { // пока не дошли до закрывающей группу скобки
            secondName = secondName.substring(0, secondName.length() - 2); // определяем, что это за фигура
            Figure tmp;
            addFigure(tmp = factory.createFigure(secondName, g)); // добавляем в группу

            if(tmp!=null) {
                tmp.load(scan, factory);

                tmp.setGroup(this);
                size++;
            }
        }
        count = size;
    }

    public void setStorageCross(StorageCross cross){
        for(int i = 0; i< count; i++)
            figInInside[i].setStorageCross(cross);
    }

    // перерисовка при движении фигуры
    public void move(int dx, int dy){
        boolean isCan = setPlace(dx, dy);

        if(!isCan) {
            return; // нельзя
        }


        for(int i = 0; i<count; i++)
            figInInside[i].move(dx, dy);

        /*for(int i = 0; i<count; i++){
            if(figInInside[i] instanceof Group){
                ((Group)figInInside[i]).mov(dx, dy);
            }else { // выделить в функцию
                figInInside[i].delete();
                figInInside[i].corX += dx;
                figInInside[i].corY += dy;
                figInInside[i].draw();
            }
        }*/
    }

    public void mov(int dx, int dy){
        for(int i = 0; i<count; i++){
            if(figInInside[i] instanceof Group)
                ((Group)figInInside[i]).mov(dx,dy);
            else{
                figInInside[i].delete();
                figInInside[i].corX += dx;
                figInInside[i].corY += dy;
                figInInside[i].draw();
            }
        }
    }

    public void setColor(Color col){
        for(int i = 0;i<count; i++)
            figInInside[i].setColor(col);
    }


    // тут где -то пролема, когда мы ищем пересечения с группой и назначаем ей наблюдателей в PAint!!
    public boolean findCrossWith(Figure tmp){
        for (int i = 0; i < count; i++) {
            if (figInInside[i] != tmp && figInInside[i].check(tmp))
                return true;
        }

        if(tmp instanceof Group){
            Group t = (Group) tmp;
            for(int i = 0; i<t.size;i++){
               if(findCrossWith(t.figInInside[i]))
                   return true;
            }
        }else {

            for (int i = 0; i < count; i++) {
                if (figInInside[i] != tmp && figInInside[i].check(tmp))
                    return true;
            }
        }
        return false; // если группа не пересекается
    }

    public boolean isCross(Figure x){
    // если х тоже группа?
        boolean flag = false;
        for(int i = 0;i<count; i++){
            if(figInInside[i].isCross(x))
                flag = true;
        }
        return flag;
    }

    /*public void addObserver(MyObserver ob){
        for(int i = 0; i<count; i++){
            figInInside[i].addObserver(ob);
        }
    }*/

    public boolean hasOberver(){
        boolean flag = false;
        for(int i = 0; i<count; i++){
            if(figInInside[i].hasOberver())
                flag = true;
        }
        return  flag;
       // return figInInside[0].hasOberver();
    }

    public void deleteObserver(){
        for(int i = 0; i<count; i++)
            figInInside[i].deleteObserver();
    }

    // можем ли мы разместить здесь группу THIS IS WORK!!!
    public boolean setPlace(int dx, int dy){
        for(int i = 0; i<count; i++)
             if (!figInInside[i].setPlace(figInInside[i].corX + dx, figInInside[i].corY + dy))
                return false;

        return true;
    }

    public void recalculate(int mode){
        boolean isCan = setPlace(0, 0);
        if(!isCan && mode==1) // если увеличиваем
            return;

        for(int i = 0; i<count; i++) {
            for(int j = i+1; j<count; j++) {

                // смотрим находится ли круг в круге
                if(figInInside[i].corX+figInInside[i].AB >= figInInside[j].corX+figInInside[j].AB &&
                        figInInside[i].corY+figInInside[i].AB >= figInInside[j].corY+figInInside[j].AB){

                }
                else if (figInInside[i].check(figInInside[j]) && mode==1) { // проверяем не пересекаются ли они при увеличении
                    return;
                }
            }

            figInInside[i].recalculate(mode);
        }
    }

    public Figure[] ungroup(Storage s){
        Figure[] masCopy = figInInside;
        for(int i = 0; i<count; i++) {
            figInInside[i].delGroup(); // говорим фигуре, что она не в группе
            s.add(figInInside[i]); // добавляем фигуру из группы обратно в хранилище
        }

        figInInside = new Figure[4]; // обнуляем группу
        count = 0;
        return masCopy;
    }

    public boolean isBlueColor(){
        if(figInInside[0] instanceof Group)
            figInInside[0].isBlueColor();

        return figInInside[0].isBlueColor();
    }

    public void setCorX(int x){
        int dx = x - figInInside[0].corX; // вычисляем смещение
        figInInside[0].corX = x; // первую фигуру ставим туда, где мышь

        for(int i = 1; i<count; i++)
            figInInside[i].corX += dx;
    }

    public void setCorY(int y){
        int dy = y - figInInside[0].corY;
        figInInside[0].corY = y;

        for(int i = 1; i<count; i++)
            figInInside[i].corY += dy;

    }

    public void doSticky(){
        for(int i = 0; i<count; i++)
            figInInside[i].doSticky();
    }

    public void doNotSticky(){
        for(int i = 0; i<count; i++)
            figInInside[i].doNotSticky();
    }

    public boolean check(Figure f){
        for(int i = 0;i<count; i++)
            if(figInInside[i].check(f))
                return true;

        return false;
    }

    public Figure[] getData(){
        return figInInside;
    }

    public int getCount(){
        return count;
    }

    public boolean isSticky(){
        return figInInside[0].isSticky();
    }

    @Override
    public boolean isInside(int x, int y) {
        for(int i = 0; i<count; i++){
            boolean inside = figInInside[i].isInside(x,y);
            if(inside)
                return true;
        }
        return false;
    }
}
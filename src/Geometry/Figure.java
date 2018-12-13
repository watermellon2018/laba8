package Geometry;

import Factory.Factory;
import GUI.Paint;
import Observer.MyObserver;
import Observer.Subject;
import Container.*;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Figure extends Subject implements MyObserver{
    protected int corX;
    protected int corY;
    protected int AB=20;
    protected Color color;
    protected int size;
    protected Graphics g;
    protected Group parent; // группа, в которой лежит фигура, нужно для разгруппировки
    protected String name; // для панеле со слоями
    private boolean sticky = false; // липкая ли фиугура
    private StorageCross storageCross;

    public Figure(int b, int c, Graphics g){
        corX = b;
        corY = c;
        size = 2;
        color = Color.GREEN;
        this.g = g;
    }

    public Figure(int b, int c, Graphics g, Color col){
        corX = b;
        corY = c;
        size = 2;
        color = col;
        this.g = g;
    }

    // добавить колесо мыши
    public void recalculate(int mode){
        size = size + ((mode==1) ? 1: -1);
        if(size == 0) {
            size++;
            return;
        }

        delete();
        //g.clearRect(corX, corY, AB+1, AB+1);
        AB = size * 20;

        if(setPlace(corX, corY))
            color = (size == 1 ? Color.RED :color);
        else{
            AB-=20;
            size = size + ((mode==1) ? -1: 1);
        }

        draw();
    }

    public void doSticky(){
        sticky = true;
    }

    public void doNotSticky(){
        sticky = false;
    }

    public void delete(){
        Color copyColor = color;
        color = new Color(238, 238, 238); // надо как-то получить доступ к фону панели!!!!
        draw();
        color = copyColor; // возвращаемся к исходномуцвету, для изменения фигур
    }

    // проверяем есть ли он в списке нажатых через родителей
    // потомки им пользуются
    protected boolean defPar(Figure f){
        if(f.parent!=null)
            return defPar(f.parent);

        return true;
    }

    public void draw(){
        g.setColor(color);
    }

    public void setStorageCross(StorageCross cross){
        storageCross = cross;
    }

    // можем ли мы переместить липкие объект, проверим выходят ли они за границы
    private boolean canMoveChild(StorageCross cross, int dx, int dy){

        for(int i = 0; i < cross.size(); i++){
            Figure tmp = cross.get(i);
            if(!tmp.setPlace(tmp.corX+dx, tmp.corY + dy))
                return false;
        }

        return true;
    }

    // фигуры тащат за собой фигуры, а те другие и т.д. Что если сделать, чтобы хранилище следило за фигурами и каждый раз, когда фигура
    // двигалась мы уведомляли хранилище о движении
    // проблема со снятие липкого объеккта
    // чтобы липкие фигуры не выходили за границы и тянули на себя
    // если обычный объект пересекаетсяя липкий
    public void move(int dx, int dy){
        if(!setPlace(corX+dx, corY+dy))
            return;

        // find cross
        /*if(sticky) {


           /* if (storageCross == null || storageCross.isEmpty()) {
            }*/// else {
                //boolean flag = canMoveChild(storageCross, dx, dy);
                //if (!flag)
                //    return;
                //notifyCross(dx, dy); // говорим фигурам, с которыми пересекаемся, что мы двигаемся и им советуем
            //}
       // }*/

       // notifyMove(dx, dy); // говорим шпионам, что мы двигаемся

            delete();
       // System.out.println(corX+" "+corY);
            corX += dx;
            corY += dy;
            draw();
        //System.out.println(corX+" "+corY);

        notifyMove(dx, dy);

    }

    public boolean setPlace(int x, int y){
        if(y <= 0 || y+AB >= Paint.heightPaint || x+AB >= Paint.weightPaint || x <= 0)
            return false;
        return true;
    }

    public void setColor(Color c){
        color = c;
    }

    public void setGroup(Group g){
        parent = g;
    }

    // используются при создании фигуры, когда проверяется можно ли ее разместить
    public int getCorX(){
        return corX;
    }

    public int getCorY(){
        return corY;
    }

    // для разгруппировки, он больше не родитель
    public void delGroup(){
        parent = null;
    }

    public boolean isBlueColor() {
        if (color.equals(Color.BLUE))
            return true;

        return false;
    }

    public boolean isPutDown(){
        return color.equals(Color.BLUE);
    }

    public void save(FileWriter file) throws IOException {
        file.write("CorX: "+String.valueOf(corX)+"\n");
        file.write("CorY: "+String.valueOf(corY)+'\n');
        file.write("AB: "+String.valueOf(AB)+'\n');

        String colorString;
         if(defPar(this))
            colorString = "0 0 255";
        else
         colorString = color.getRed()+" "+color.getGreen()+" "+color.getBlue();

        file.write("Color: "+colorString+'\n');

        file.append('}');
        file.append('\n');
        file.flush();
    }

    public void load(Scanner scan, Factory factory){

        int[] data = new int[6]; // для хранения данных
        Pattern number = Pattern.compile("(\\d+)"); // от пробела и число
        Matcher mat;
        for (int i = 0; i<3 ;i++){
            mat = number.matcher(scan.nextLine()); mat.find();
            data[i] = Integer.valueOf(mat.group());
        }

        Pattern colors = Pattern.compile("(\\d+)");
        Matcher color1 = colors.matcher(scan.nextLine());

        for(int i = 3; color1.find(); i++)
            data[i] = Integer.valueOf(color1.group());

        Color newCol = new Color(data[3],data[4],data[5]);
        scan.nextLine(); // читаем просто символ '}'
        // закончили чтения необходимых данных

        corX = data[0]; corY = data[1]; AB = data[2];
        color = newCol;

    }

    // использует при вставке на панель ctrl+V
    public void setCorX(int x){
        corX = x;
    }

    public void setCorY(int y){
        corY = y;
    }

    // проверка пересекается ли с кем-то фигура, нужна для изменения размера и местоположения фигуры
    // при увеличении размера проблемы, если большая фигура слева, то макс, а если справа, то мин
    public boolean check(Figure f){
        // пересекает ли
        if(f instanceof Group){
            // мы должны проверить для каждой фигуры из группы
            // меняемся местами для группы проверяем не перескается ли кто-то там с этой фигурой. WORK!!!!
            if(((Group) f).check(this))
                return true;

        }/*else if(Math.abs(corX-f.corX)<=Integer.max(AB, f.AB) && Math.abs(corY-f.corY)<=Integer.max(AB, f.AB))
            return true;*/
        else{

           double d = Math.sqrt((corX-f.corX)*(corX-f.corX)+(corY-f.corY)*(corY-f.corY));
           if(d<=Integer.min(AB, f.AB) && (corX>=f.corX || corY<=f.corY)) // большой справа или снизу
               return true;
           if(d<=Integer.max(AB,f.AB) && (corX<=f.corX || corY>=f.corY))
               return true;
           // if(d<=Integer.max(AB,f.AB)) // || d<=Integer.min(AB, f.AB)
             //   return true;

        }
        return false;
    }

    public String getName(){
        return name;
    }

    // если мы ходим переименовать слой
    public void setName(String newName){
        name = newName;
    }

    public boolean isSticky(){
        return sticky;
    }

    public boolean isCross(Figure x){
        if(check(x)) {
            addObserver(x); // фигура, которая с ним пересекается теперь следит за ним
            return true;
        }

        return false;
    }


    @Override
    public void stickyMove(Subject who, int dx, int dy) {
        Figure f = (Figure) who;
        if(f.isSticky()){
            // если липкая
            move(dx, dy);
        }else{
            draw();
        }


       /* Figure f = (Figure) who; // та, которая хочет двигаться

        if(this!= f && this.check(f)) // если фигура с кем-то пересекается, то добавляем ее в список пересеченных
            this.move(dx, dy);*/
    }

    @Override
    public void onSubjectChanged(Subject who) {
    }

    @Override
    public void setSelect(Subject who) {
    }

    public abstract boolean isInside(int x, int y);
}
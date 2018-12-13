
package Commands;

import GUI.Paint;
import Geometry.Figure;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class Command {
 //  protected Figure copy;
   protected Paint paint;
   protected Deque<Figure> copyFig;

    public Command(Paint p){
        paint = p;
        copyFig = new ArrayDeque<>(); // сюда добавляем фигуры, которые копируем
    }

    public void undo(){
        copyFig.pollLast().delete(); // стираем фигуру
    }

    public void backup(){
        copyFig.add(paint.getCopyFigure());
        //copy = paint.getCopyFigure();
    }
    public abstract boolean execute();
}


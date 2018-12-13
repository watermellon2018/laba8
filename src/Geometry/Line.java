package Geometry;

import GUI.Paint;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class Line extends Figure {
    private static int numberFig = 1; // для названия по умолчанию


    public Line(int b, int c, Graphics g) {
        super(b, c, g);
        name = "Line"+numberFig;
        numberFig++;
        AB = size * AB;// когда мы поменяем масштаб, сторона пересчитается
    }

    public Line(int b, int c, Graphics g, Color col) {
        super(b, c, g, col);
        name = "Line"+numberFig;
        numberFig++;
        AB = size * AB;// когда мы поменяем масштаб, сторона пересчитается
    }

    public void recalculate(int mode){
        size = size + ((mode==1) ? 1: -1);
        g.clearRect(corX, corY, AB+1, 1);
        AB = size * 20;
        color = (size==1 ? Color.RED : Color.BLUE);

        draw();
    }

    public void delete(){
        g.clearRect(corX,corY, AB+1, 1);
    }

    public void draw(){
        super.draw();
        g.drawLine(corX, corY, corX+AB, corY);
    }

    public void save(FileWriter file) throws IOException {
        file.write("Line {");
        file.append('\n');
        super.save(file);
    }

    @Override
    public boolean setPlace(int x, int y) {
        if(x+AB> Paint.weightPaint || x<0 || y<0 || y>Paint.heightPaint)
            return false;
        return true;
    }

    @Override
    public boolean isInside(int x, int y) {
        if(x>=corX && (corY+5>=y && corY-5<=y))
            return true;
        return false;
    }
}
package Geometry;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class Triangle extends Figure { // равностороний
    private static int numberFig = 1; // для названия по умолчанию


    public Triangle(int corX, int corY, Graphics g){
        super(corX, corY-20, g);
        name = "Triangle"+numberFig;
        numberFig++;
        AB = size * AB;// когда мы поменяем масштаб, сторона пересчитается
    }

    // почему ты не светишься!!!!!
    public Triangle(int corX, int corY, Graphics g, Color color){
        super(corX, corY-20, g, color);
        name = "Triangle"+numberFig;
        numberFig++;
        AB = size * AB;// когда мы поменяем масштаб, сторона пересчитается
    }


    @Override
    public boolean isInside(int x, int y){
        int a = (corX - x) * (corY - (corY+AB/2)) - (corX + AB - corX) * (corY+AB/2 - y);
        int b = (corX + AB - x) * (corY + AB - corY) - (corX + AB  - (corX + AB)) * (corY - y);
        int c = (corX + AB - x) * (corY + AB/2 - (corY + AB)) - (corX - (corX + AB)) * (corY + AB - y);


        if((a>=0 && b>=0 && c>=0) || (a<=0 && b<=0 && c<=0))
            return true;
        return false;
    }

    public void draw() {
        super.draw();
        g.drawPolygon(new int[]{corX,corX+AB,corX+AB}, new int[]{corY+AB/2,corY,corY+AB}, 3);
    }

    public void save(FileWriter file) throws IOException {
        file.write("Triangle {");
        file.append('\n');
        super.save(file);
    }

    public void pour(){
        super.draw();
        g.fillPolygon(new int[]{corX,corX+AB,corX+AB}, new int[]{corY+AB/2,corY,corY+AB}, 3);
    }
}
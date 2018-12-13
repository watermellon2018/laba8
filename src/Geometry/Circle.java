package Geometry;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class Circle extends Figure {

    private static int numberFig = 1; // для названия по умолчанию

    public Circle(int b, int c,Graphics g) {
        super(b-20, c-20, g);
        name = "Circle"+numberFig;
        numberFig++;
        AB = AB * size;
    }

    // при нажатии
    public Circle(int b, int c,Graphics g, Color col) {
        super(b-20, c-20, g, col);
        name = "Circle"+numberFig;
        numberFig++;
        AB = AB * size;
    }



    public void draw() {
        super.draw();
        g.drawOval(corX, corY, AB, AB);
    }

    public void save(FileWriter file) throws IOException {
        file.write("Circle {");
        file.append('\n');

        super.save(file);
    }


    @Override
    public boolean isInside(int x, int y){
        int a = (x - corX-AB/2)*(x - corX-AB/2) + (y - corY-AB/2)*(y - corY-AB/2);
        return a <= AB*AB/4;
    }
}
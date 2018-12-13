package Geometry;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class Rectangle extends Figure {
  //квадрат??

    private static int numberFig = 1; // для названия по умолчанию

    public Rectangle(int b, int c, Graphics g) {
        super(b, c, g);
        name = "Rectangle"+numberFig;
        numberFig++;
        AB = size * AB;
    }

    public Rectangle(int b, int c, Graphics g, Color color) {
        super(b, c, g, color);
        name = "Rectangle"+numberFig;
        numberFig++;
        AB = size * AB;
    }

    public void draw() {
        super.draw();
        g.drawRect(corX, corY, AB, AB);
    }

    public void pour(){
        super.draw();
        g.fillRect(corX, corY, AB, AB);
    }

    public void save(FileWriter file) throws IOException {
        file.write("Rectangle {");
        file.append('\n');
        super.save(file);
    }

    @Override
    public boolean isInside(int x, int y){
        if(corX <= x && x <= corX+AB && corY <= y && y <= corY+AB)
            return true;
        return false;
    }
}
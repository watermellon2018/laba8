package Factory;

import Geometry.*;
import Geometry.Rectangle;

import java.awt.*;


public class MyFactory extends Factory {

    @Override
    public Figure createFigure(String nameFig, Graphics g) {
        Figure figure = null;
        switch (nameFig){
            case "Circle":
                figure = new Circle(0,0, g);
                break;
            case "Rectangle":
                figure = new Rectangle(0,0, g);
                break;
            case "Triangle":
                figure = new Triangle(0,0, g);
                break;
            case "Line":
                figure = new Line(0,0, g);
                break;
            case "Group":
                figure = new Group(0,0,g);
                break;
        }

        return figure;

    }
}
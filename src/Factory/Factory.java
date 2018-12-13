package Factory;

import Geometry.Figure;

import java.awt.*;

public abstract class Factory {
    public abstract Figure createFigure(String nameFigure, Graphics g);
}

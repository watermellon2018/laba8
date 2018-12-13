
package Commands;

import GUI.Paint;

public class PasteCommand extends Command {

    public PasteCommand(Paint p) {
        super(p);
    }

    @Override
    public boolean execute() {
        if(paint.getCopyFigure() == null)
            return false;

        paint.insertToPanel();
        backup(); // запоминаем, какую фигуру скопировали

        return true;
    }
}


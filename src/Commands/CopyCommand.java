
package Commands;

import GUI.Paint;

public class CopyCommand extends Command {

    public CopyCommand(Paint p) {
        super(p);
    }

    @Override
    public boolean execute() {
        paint.setCopyFigure(); // присовить объект, отвечающий за копированную фигуру текущий указатель на фигуру
        return false;
    }
}


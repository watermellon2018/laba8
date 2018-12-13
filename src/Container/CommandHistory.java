package Container;

import Commands.Command;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandHistory {
    private Deque<Command> history;

    public CommandHistory(){
        history = new ArrayDeque<>();
    }

    public void push(Command c) {
        history.push(c);
    }

    public Command pop() {
        return history.pop();
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }
}
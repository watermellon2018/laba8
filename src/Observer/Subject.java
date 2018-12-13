package Observer;


import java.util.ArrayList;

public class Subject {
    private ArrayList<MyObserver> observers;

    public Subject(){
        observers = new ArrayList<MyObserver>();
    }

    public void addObserver(MyObserver ob){
        observers.add(ob);
    }

    public boolean hasOberver(){
        return !observers.isEmpty();
    }

    public void deleteObserver(){
        observers.clear();
    }

    public void notifyEveryone(){
        for(MyObserver x : observers)
            x.onSubjectChanged(this);
    }



    public void notifySelect(){
        for(MyObserver x : observers)
            x.setSelect(this);
    }

    public void notifyStickyMove(int dx, int dy){
        for(MyObserver x : observers)
            x.stickyMove(this, dx, dy);
    }

    /*public void notifyCross(int dx, int dy){
        for(MyObserver x : observers)
            x.stickyMove(this, dx, dy);
    }*/

    public void notifyMove(int dx, int dy){
        for(MyObserver x: observers)
            x.stickyMove(this, dx,dy);

    }

    /*public void notifyMove(){
        for(MyObserver x : observers)
            x.findCross(this);
    }*/

}

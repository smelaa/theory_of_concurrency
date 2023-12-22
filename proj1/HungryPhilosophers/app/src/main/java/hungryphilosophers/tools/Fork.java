package hungryphilosophers.tools;

import java.util.concurrent.locks.ReentrantLock;

public class Fork{
    private Boolean busy = false;
    public ReentrantLock rLock = new ReentrantLock();

    public void giveBack() {
        busy = false;
        this.notify();
    }

    public void take() {
        busy = true;
    }

    public Boolean isBusy() {
        return busy;
    }
}

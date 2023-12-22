package hungryphilosophers.philosophers;

import hungryphilosophers.Parameters;
import hungryphilosophers.tools.Fork;
import hungryphilosophers.tools.TimeCounter;

public class Philosopher2 extends Thread {
    private int id;
    private Fork lFork;
    private Fork rFork;
    private TimeCounter cnt;

    public Philosopher2(int n, Fork right, Fork left, TimeCounter c) {
        id = n;
        lFork = left;
        rFork = right;
        cnt=c;
    }

    public void run() {
        try {
            sleep(Parameters.waitingTime);
        } catch (Exception e) {
            System.out.printf("Exception0 happened!", e);
        }
        boolean lLocked;
        boolean rLocked;
        long start;
        long end;
        while (!Parameters.stopCondition) {
            try {
                start=System.nanoTime();
                if(Parameters.printInfo) System.out.println(id + ":\tI am thinking");
                lLocked = false;
                rLocked = false; 
                while (!(lLocked && rLocked)) {
                    if (lFork.rLock.isHeldByCurrentThread()) {
                        lFork.rLock.unlock();
                    } else if (rFork.rLock.isHeldByCurrentThread()){
                        rFork.rLock.unlock();
                    }
                    lLocked = lFork.rLock.tryLock();
                    rLocked = rFork.rLock.tryLock();
                }
                end=System.nanoTime();
                if(Parameters.printInfo) System.out.println(id + ":\tI am eating");
                sleep(Parameters.eatingTime);
                lFork.rLock.unlock();
                rFork.rLock.unlock();
                cnt.increment(end-start);
            } catch (Exception e) {
                System.out.printf("Exception1 happened!", e);
            }

        }
    }
}

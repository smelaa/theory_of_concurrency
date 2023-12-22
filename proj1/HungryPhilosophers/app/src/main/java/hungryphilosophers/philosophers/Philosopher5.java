package hungryphilosophers.philosophers;

import java.util.concurrent.Semaphore;

import hungryphilosophers.Parameters;
import hungryphilosophers.tools.Fork;
import hungryphilosophers.tools.TimeCounter;

public class Philosopher5 extends Thread {
    private int id;
    private Fork lFork;
    private Fork rFork;
    static private Semaphore sem = new Semaphore(Parameters.n);
    private TimeCounter cnt;

    public Philosopher5(int n, Fork right, Fork left, TimeCounter c) {
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
        long start;
        long end;
        while (!Parameters.stopCondition) {
            try {
                if(Parameters.printInfo) System.out.println(id + ":\tI am thinking");
                start=System.nanoTime();
                sem.acquire();
                synchronized (lFork) {
                    while (lFork.isBusy()) {
                        lFork.wait();
                    }
                    lFork.take();
                    synchronized (rFork) {
                        while (rFork.isBusy()) {
                            rFork.wait();
                        }
                        rFork.take();
                        end=System.nanoTime();
                        if(Parameters.printInfo) System.out.println(id + ":\tI am eating");
                        sleep(Parameters.eatingTime);
                        rFork.giveBack();
                    }
                    lFork.giveBack();
                }
                sem.release();
                cnt.increment(end-start);
            } catch (Exception e) {
                System.out.printf("Exception1 happened!", e);
            }

        }
    }
}

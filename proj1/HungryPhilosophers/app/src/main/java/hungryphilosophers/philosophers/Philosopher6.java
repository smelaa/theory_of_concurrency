package hungryphilosophers.philosophers;

import java.util.concurrent.Semaphore;

import hungryphilosophers.Parameters;
import hungryphilosophers.tools.Fork;
import hungryphilosophers.tools.TimeCounter;

public class Philosopher6 extends Thread {
    private int id;
    private Fork lFork;
    private Fork rFork;
    static private Semaphore sem = new Semaphore(Parameters.n);
    private TimeCounter cnt;

    public Philosopher6(int n, Fork right, Fork left, TimeCounter c) {
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
        Fork firstFork = lFork;
        Fork secondFork = rFork;
        long start;
        long end;
        while (!Parameters.stopCondition) {
            try {
                if(Parameters.printInfo) System.out.println(id + ":\tI am thinking");
                start=System.nanoTime();
                boolean isInDiningRoom = sem.tryAcquire();
                if (!isInDiningRoom) {
                    firstFork = rFork;
                    secondFork = lFork;
                }
                synchronized (firstFork) {
                    while (firstFork.isBusy()) {
                        firstFork.wait();
                    }
                    firstFork.take();
                    synchronized (secondFork) {
                        while (secondFork.isBusy()) {
                            secondFork.wait();
                        }
                        secondFork.take();
                        end=System.nanoTime();
                        if(Parameters.printInfo) System.out.println(id + ":\tI am eating");
                        sleep(Parameters.eatingTime);
                        secondFork.giveBack();
                    }
                    firstFork.giveBack();
                }
                if (!isInDiningRoom) {
                    firstFork = lFork;
                    secondFork = rFork;
                } else {
                    sem.release();
                }
                cnt.increment(end-start);
            } catch (Exception e) {
                System.out.printf("Exception1 happened!", e);
            }

        }
    }
}

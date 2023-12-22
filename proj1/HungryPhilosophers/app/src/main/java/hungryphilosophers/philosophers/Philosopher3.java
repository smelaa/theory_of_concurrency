package hungryphilosophers.philosophers;

import hungryphilosophers.Parameters;
import hungryphilosophers.tools.Fork;
import hungryphilosophers.tools.TimeCounter;

public class Philosopher3 extends Thread {
    private int id;
    private Fork firstFork;
    private Fork secondFork;
    private TimeCounter cnt;

    public Philosopher3(int n, Fork right, Fork left, TimeCounter c) {
        id = n;
        if (n % 2 == 0) {
            firstFork = right;
            secondFork = left;
        } else {
            firstFork = left;
            secondFork = right;
        }
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
                    cnt.increment(end-start);
                }
            } catch (Exception e) {
                System.out.printf("Exception1 happened!", e);
            }
        }
    }
}

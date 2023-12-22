package hungryphilosophers.philosophers;

import java.util.Random;

import hungryphilosophers.Parameters;
import hungryphilosophers.tools.Fork;
import hungryphilosophers.tools.TimeCounter;

public class Philosopher4 extends Thread {
    private int id;
    private Fork lFork;
    private Fork rFork;
    private Fork firstFork;
    private Fork secondFork;
    private Random rand = new Random();
    private TimeCounter cnt;

    public Philosopher4(int n, Fork right, Fork left, TimeCounter c) {
        id = n;
        lFork = left;
        rFork = right;
        cnt=c;
    }

    private void decideOnFork() {
        firstFork = lFork;
        secondFork = rFork;
        if (rand.nextInt(1) == 0) {
            firstFork = rFork;
            secondFork = lFork;
        }
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
                decideOnFork();
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

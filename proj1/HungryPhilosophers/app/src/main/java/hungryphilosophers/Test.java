package hungryphilosophers;

import java.util.LinkedList;

import hungryphilosophers.philosophers.Philosopher1;
import hungryphilosophers.philosophers.Philosopher2;
import hungryphilosophers.philosophers.Philosopher3;
import hungryphilosophers.philosophers.Philosopher4;
import hungryphilosophers.philosophers.Philosopher5;
import hungryphilosophers.philosophers.Philosopher6;
import hungryphilosophers.tools.Fork;
import hungryphilosophers.tools.Philosopher;
import hungryphilosophers.tools.TimeCounter;
import hungryphilosophers.tools.TimeCountersGroup;

public class Test {
    private LinkedList<Thread> philosophersRunning= new LinkedList<>();
    public TimeCountersGroup runTest(Philosopher type) {
        Fork leftCurr = new Fork();
        Fork rightCurr = new Fork();
        Fork firstFork = rightCurr;
        TimeCountersGroup cntGroup = new TimeCountersGroup();
        for (int i = 0; i < Parameters.n; i++) {
            TimeCounter timeCounter = new TimeCounter();
            cntGroup.addCounter(timeCounter);
            Thread p=switch (type) {
                case P1 ->
                    new Philosopher1(i, leftCurr, rightCurr, timeCounter);
                case P2 ->
                    new Philosopher2(i, leftCurr, rightCurr, timeCounter);
                case P3 ->
                    new Philosopher3(i, leftCurr, rightCurr, timeCounter);
                case P4 ->
                    new Philosopher4(i, leftCurr, rightCurr, timeCounter);
                case P5 ->
                    new Philosopher5(i, leftCurr, rightCurr, timeCounter);
                case P6 ->
                    new Philosopher6(i, leftCurr, rightCurr, timeCounter);
            };
            rightCurr = leftCurr;
            if (i < Parameters.n - 1) {
                leftCurr = new Fork();
            } else {
                leftCurr = firstFork;
            }
            philosophersRunning.add(p);
            p.start();
        }
        return cntGroup;
    }

    public void stopAndCheckIfRunning(TimeCountersGroup countersGroup){
        int cnt=0;
        for (Thread p: philosophersRunning){
            if (p.isAlive()){cnt++;}
            p.interrupt();
        }
        countersGroup.addBlocked(cnt);
    }
}

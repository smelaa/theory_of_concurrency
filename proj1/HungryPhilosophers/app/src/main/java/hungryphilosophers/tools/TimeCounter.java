package hungryphilosophers.tools;

import java.util.LinkedList;

public class TimeCounter {
    private long timeElapsed=0;
    private int n=0;
    private LinkedList<Long> times = new LinkedList<>();

    public void increment(long time){
        n++;
        timeElapsed+=time;
        times.add(Long.valueOf(time));
    }

    public long getTimeElapsed(){
        return timeElapsed;
    }

    public int getN(){
        return n;
    }

    public Long getMean(){
        return timeElapsed/n;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (Long val: times){
            builder.append(Long.toString(val));
            builder.append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}

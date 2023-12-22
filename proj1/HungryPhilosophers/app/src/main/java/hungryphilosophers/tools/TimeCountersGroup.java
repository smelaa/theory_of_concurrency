package hungryphilosophers.tools;
import java.util.LinkedList;

public class TimeCountersGroup {
    LinkedList<TimeCounter> cnts=new LinkedList<>();
    int blockedCnt=0;

    public void addCounter(TimeCounter cnt){
        cnts.add(cnt);
    }

    public void addBlocked(int n){
        blockedCnt+=n;
    }

    public long findOneMean(){
        long timeTotal=0;
        int nTotal=0;
        for(TimeCounter cnt: cnts){
            timeTotal+=cnt.getTimeElapsed();
            nTotal+=cnt.getN();
        }
        return timeTotal/nTotal;
    }

    public int getBlocked(){
        return blockedCnt;
    }

    public Long getMean(){
        int n=0;
        Long sum=Long.valueOf(0);
        for(TimeCounter cnt: cnts){
            sum+=cnt.getMean();
            n++;
        }
        return sum/n;
    }

    public Long getAvgSum(){
        int n=0;
        Long sum=Long.valueOf(0);
        for(TimeCounter cnt: cnts){
            sum+=cnt.getTimeElapsed();
            n++;
        }
        return sum/n;
    }

    public Long getMaxMean(){
        Long res=Long.valueOf(0);
        for(TimeCounter cnt: cnts){
            res=Math.max(res, cnt.getMean());
        }
        return res;
    }

    public Long getAvgN(){
        int n=0;
        Long sum=Long.valueOf(0);
        for(TimeCounter cnt: cnts){
            sum+=cnt.getN();
            n++;
        }
        return sum/n;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (TimeCounter cnt: cnts){
            builder.append(cnt.toString());
            builder.append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}

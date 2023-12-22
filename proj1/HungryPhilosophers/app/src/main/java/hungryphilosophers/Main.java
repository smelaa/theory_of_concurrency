package hungryphilosophers;

import hungryphilosophers.tools.Philosopher;
import hungryphilosophers.tools.TimeCountersGroup;
import java.io.FileWriter;
import java.lang.StringBuilder;
import java.io.File;

public class Main {
    private static TimeCountersGroup runTest(Philosopher p) {
        Parameters.stopCondition = false;
        Test test= new Test();
        TimeCountersGroup cntGroup = test.runTest(p);
        try {
            Thread.sleep(Parameters.testTime);
            Parameters.stopCondition = true;
            Thread.sleep(Parameters.waitingTime);
            test.stopAndCheckIfRunning(cntGroup);
        } catch (Exception e) {
            System.out.printf("Exceptions happen!", e);
        }
        return cntGroup;
    }

    private static void saveToBasicFile(TimeCountersGroup[][] cntGroups){
        try{
            File meanFile = new File("data_mean.csv");
            File sumFile = new File("data_sum.csv");
            File nFile = new File("data_n.csv");
            File maxFile = new File("data_max.csv");
            File blockedFile = new File("data_blocked.csv");
            FileWriter meanFileWriter = new FileWriter(meanFile);
            FileWriter sumFileWriter = new FileWriter(sumFile);
            FileWriter nFileWriter = new FileWriter(nFile);
            FileWriter maxFileWriter = new FileWriter(maxFile);
            FileWriter blockedFileWriter = new FileWriter(blockedFile);
            for (TimeCountersGroup[] data : cntGroups) {
                StringBuilder meanLine = new StringBuilder();
                StringBuilder sumLine = new StringBuilder();
                StringBuilder nLine = new StringBuilder();
                StringBuilder maxLine = new StringBuilder();
                StringBuilder blockedLine = new StringBuilder();
                for (int i = 0; i < data.length; i++) {
                    meanLine.append(data[i].getMean().toString());
                    sumLine.append(data[i].getAvgSum().toString());
                    nLine.append(data[i].getAvgN().toString());
                    maxLine.append(data[i].getMaxMean().toString());
                    blockedLine.append(Integer.toString(data[i].getBlocked()));
                    if (i != data.length - 1) {
                        meanLine.append(',');
                        sumLine.append(',');
                        nLine.append(',');
                        maxLine.append(',');
                        blockedLine.append(',');
                    }
                }
                meanLine.append("\n");
                sumLine.append('\n');
                nLine.append('\n');
                maxLine.append('\n');
                blockedLine.append('\n');
    
                meanFileWriter.write(meanLine.toString());
                sumFileWriter.write(sumLine.toString());
                nFileWriter.write(nLine.toString());
                maxFileWriter.write(maxLine.toString());
                blockedFileWriter.write(blockedLine.toString());
            }
            meanFileWriter.close();
            sumFileWriter.close();
            nFileWriter.close();
            maxFileWriter.close();
            blockedFileWriter.close();
        } catch (Exception e) {
            System.out.printf("Exceptions happen!", e);
        }  
    }

    private static void saveToComplexFile(TimeCountersGroup[][] cntGroups, int[] numbersOfPhilosophers){
        try{
            for (int i=0;i<numbersOfPhilosophers.length;i++) {
                for (Philosopher p : Philosopher.values()) {
                    File waitingTimesFile = new File("data_wt_"+Integer.toString(p.order())+"_"+Integer.toString(numbersOfPhilosophers[i])+".csv");
                    FileWriter waitingFileWriter = new FileWriter(waitingTimesFile);
                    waitingFileWriter.write(cntGroups[i][p.order()].toString());
                    waitingFileWriter.close();
            }
        }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("Exceptions happen!", e);
        }  
    }

    public static void main(String args[]) {
        // System.out.println("Basic tests...\n");
        // int[] nBasicTests = { 5, 6, 8, 10, 15, 20, 30, 50, 75, 100, 150, 200 };
        // TimeCountersGroup[][] cntGroupsBasic = new TimeCountersGroup[nBasicTests.length][6];
        // for (int i = 0; i < nBasicTests.length; i++) {
        //     Parameters.n = nBasicTests[i];
        //     for (Philosopher p : Philosopher.values()) {
        //         System.out.printf("Starting: %d philosophers, solution %d.\n", nBasicTests[i], p.order());
        //         cntGroupsBasic[i][p.order()] = runTest(p);
        //         System.out.println("Done.\n");
        //     }
        // }
        // saveToBasicFile(cntGroupsBasic);
        // System.out.println("Data saved.\n");

        System.out.println("Complex tests...\n");
        int[] nComplexTests = { 5, 20, 100};
        TimeCountersGroup[][] cntGroupsComplex = new TimeCountersGroup[nComplexTests.length][6];
        for (int i = 0; i < nComplexTests.length; i++) {
            Parameters.n = nComplexTests[i];
            for (Philosopher p : Philosopher.values()) {
                System.out.printf("Starting: %d philosophers, solution %d.\n", nComplexTests[i], p.order());
                cntGroupsComplex[i][p.order()] = runTest(p);
                System.out.println("Done.\n");
            }
        }
        saveToComplexFile(cntGroupsComplex, nComplexTests);
        System.out.println("Data saved.\n");
    }
}

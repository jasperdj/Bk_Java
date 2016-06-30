package com.testing;

import com.db.Database;
import com.routeHelpers.dataTypes.EventData;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.toIntExact;

/**
 * Created by a623557 on 3-6-2016.
 */
public class DatabaseDriverPerformanceTest {
    Database database = Database.getInstance();
    Random randomInt = new Random();
    Integer iterations = 5;
    Integer subIterations = 1000;
    String prototype = "BK";
    String techStack = "Java";
    Long startTimestamp = time();


    public static void main(String[] args) {
        try {
            new DatabaseDriverPerformanceTest();
        }catch (Exception e) {
            System.out.println("Internal server error: " + e);
        }
    }

    public DatabaseDriverPerformanceTest() throws Exception {
        Long t1 = time();
        System.out.println(getDatetime()+": benchmarking with "+ iterations +" iterations and "+ subIterations +" sub-iterations. ");
        benchmarkQuery("insert", QueryType.insert);
        benchmarkQuery("spaceStats", QueryType.spaceStats);
        benchmarkQuery("messageStats", QueryType.messageStats);
        System.out.println(getDatetime()+": benchmarking completed in "+ toIntExact(time()-t1)+" ms.");
    }

    public void benchmarkQuery(String name, QueryType queryType) throws Exception {
        System.out.println("    Started " + name + " benchmarkQuery.");
        Long t1 = time();
        pause(queryType);
        parseTestResults(name, true, false, queryType);
        pause(queryType);
        parseTestResults(name, true, true, queryType);
        System.out.println("    Finished " + name + " benchmarkQuery in " + toIntExact(time() - t1) + " ms \n");
    }

    public void pause(QueryType type) throws Exception {
        if (type == QueryType.insert) resetDatabase();
        else sleep();
    }


    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

    public void parseTestResults(String name, Boolean pause, Boolean parallel, QueryType queryType) throws Exception {
        ArrayList<Integer> list = null;
        if (parallel)
            list = parralelTest(pause, queryType);
        else
            list = sequentialTest(pause, queryType);

        Statistics stats = new Statistics(convertIntegers(list));

        String[] dataRow = new String[]{startTimestamp.toString(), iterations.toString(), subIterations.toString(),
                prototype, techStack, name, pause.toString(), parallel.toString(), stats.min().toString(), Integer.valueOf(stats.get25percentile()).toString(),
                Double.valueOf(stats.median()).toString(),Integer.valueOf(stats.get75percentile()).toString(), Integer.valueOf(stats.get99percentile()).toString(), stats.stdError().toString()};
        String dataRowString = String.join(",", dataRow) + "\n";

        Files.write(Paths.get("databaseBenchmark.txt"), dataRowString.getBytes(), StandardOpenOption.APPEND);
        System.out.println(name + " test results (min: " + stats.min() + ", avg: " + stats.mean() + ", median: " + stats.median() + ", max: " + stats.max() + ", error: " + stats.stdError());
    }

    private enum QueryType {
        insert, messageStats, spaceStats
    }

    private static String getDatetime() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date());
    }

    private ArrayList<Integer> parralelTest(Boolean sleep, final QueryType queryType) throws Exception {
        ArrayList<Integer> results = new ArrayList<Integer>();
        for(int i = 0; i < iterations; i++) {
            Long t1 = time();
            List<Thread> threads = new ArrayList<Thread>();
            for (int b = 0; b < subIterations; b++) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            switch (queryType) {
                                case insert:
                                    database.insertEvent(new EventData().set(randomInt.nextInt(9), randomInt.nextInt(9), randomInt.nextInt(5)));
                                    break;
                                case messageStats:
                                    database.getMessageStats(randomInt.nextInt(9));
                                    break;
                                case spaceStats:
                                    database.getSpaceStats(randomInt.nextInt(9));
                                    break;
                                default:
                                    System.out.println("Could not find queryType.");
                                    break;
                            }
                        } catch (Exception e) { }
                    }
                });
                thread.start();
                threads.add(thread);
            }
            for (Thread thread : threads) thread.join();
            results.add(toIntExact(time()-t1));
            if (sleep) sleep(false);
        }

        return results;
    }

    private ArrayList<Integer> sequentialTest(Boolean sleep, QueryType queryType) throws Exception {
        ArrayList<Integer> results = new ArrayList<Integer>();
        for(int i = 0; i < iterations; i++) {
            Long t1 = time();
            for (int b = 0; b < subIterations; b++) {
                try {
                    switch (queryType) {
                        case insert:
                            database.insertEvent(new EventData().set(randomInt.nextInt(9) , randomInt.nextInt(9), randomInt.nextInt(5)));
                            break;
                        case messageStats:
                            database.getMessageStats(randomInt.nextInt(9) );
                            break;
                        case spaceStats:
                            database.getSpaceStats(randomInt.nextInt(9));
                            break;
                    }
                } catch (Exception e) { }
            }
            results.add(toIntExact(time()-t1));
            if (sleep) sleep(false);
        }

        return results;
    }

    private Long time() {
        return System.currentTimeMillis();
    }

    private void resetDatabase() throws Exception {
        System.out.println("    Database is emptied (" + database.resetDatabase().toString() + ")");
        sleep();
    }

    private void sleep() throws Exception {
        sleep(false);
    }

    private void sleep(Boolean print) throws Exception {
        Thread.sleep(1000);
        if (print) System.out.println("Sleeping 1000ms...");
    }

}

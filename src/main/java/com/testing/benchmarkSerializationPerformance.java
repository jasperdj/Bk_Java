package com.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.routeHelpers.dataTypes.EventData;

import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Exchanger;

import static java.lang.Math.toIntExact;

/**
 * Created by a623557 on 7-6-2016.
 */
public class benchmarkSerializationPerformance {
    Long startTimestamp = time();
    Integer iterations = 20;
    Integer subIterations = 20000;
    String prototype = "BK";
    String techStack = "Java";

    ObjectMapper mapper = new ObjectMapper();
    Random random = new Random();

    public static void main(String[] arg) {
        try {
            new benchmarkSerializationPerformance();
        } catch(Exception e) { System.out.println("Error: "+ e); }
    }

    private Long time() { return System.currentTimeMillis(); }

    public benchmarkSerializationPerformance() throws Exception {
        parseTestResults("read", true, SerializationType.read);
        parseTestResults("read", false, SerializationType.read);

        parseTestResults("write", true, SerializationType.write);
        parseTestResults("write", false, SerializationType.write);

        parseTestResults("mix", true, SerializationType.mix);
        parseTestResults("mix", false, SerializationType.mix);
        System.out.println("Done.");
    }

    private void mix(EventData eventData) throws Exception {
        read(write(eventData));
    }

    private String write(EventData eventData) throws Exception {
        return mapper.writeValueAsString(eventData);
    }

    private EventData read(String json) throws Exception {
        return mapper.readValue(json, EventData.class);
    }

    private enum SerializationType {
        read, write, mix
    }

    private EventData getEventData() {
        return new EventData().set(random.nextInt(9), random.nextInt(9), random.nextInt(5));
    }

    private String getJson() {
        return "{\"spaceId\":"+random.nextInt(9)+", \"messageId\": "+random.nextInt(9)+", \"eventType\": "+random.nextInt(5)+"}";
    }

    private void test(SerializationType type) {
        try {
            switch (type) {
                case read:
                    read(getJson());
                    break;
                case write:
                    write(getEventData());
                    break;
                case mix:
                    mix(getEventData());
                    break;
            }
        } catch(Exception e) { System.out.println("Error: " + e); }
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

    private void parseTestResults(String name, Boolean parallel, SerializationType type) throws Exception {
        ArrayList<Integer> data = new ArrayList<Integer>();
        if (parallel)
            data = parallelTest(type);
        else
            data = sequentialTest(type);

        Statistics stats = new Statistics(convertIntegers(data));
        String[] rowData = new String[]{iterations.toString(), subIterations.toString(), prototype, techStack,
        name, parallel.toString(), stats.min().toString(), stats.mean().toString(), stats.median().toString(),
        stats.max().toString(), stats.stdError().toString()};

        String rowDataString = String.join(",", rowData) + "\n";
        Files.write(Paths.get("serializationBenchmark.txt"), rowDataString.getBytes(), StandardOpenOption.APPEND);
    }

    private ArrayList<Integer> sequentialTest(SerializationType type) throws Exception {
        ArrayList<Integer> results = new ArrayList<Integer>();
        for(int i = 0; i < iterations; i++) {
            Long t1 = time();
            for(int b = 0; b < subIterations; b++) {
                test(type);
            }
            results.add(toIntExact(time() - t1));
        }
        return results;
    }

    private ArrayList<Integer> parallelTest(final SerializationType type) throws Exception {
        ArrayList<Integer> results = new ArrayList<Integer>();
        for(int i =0; i < iterations; i++) {
            Long t1 = time();
            ArrayList<Thread> threads = new ArrayList<Thread>();
            for(int b = 0; b < subIterations; b++) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        test(type);
                    }
                });
                thread.start();
                threads.add(thread);
            }

            for(Thread thread : threads) thread.join();
            results.add(toIntExact(time()-t1));
        }
        return results;
    }
}
package com.routeHelpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Monitor implements Runnable {
    private static Monitor instance = null;

    protected Monitor() {  }

    public static Monitor getInstance() {
        if(instance == null) {
            instance = new Monitor();
            new Thread(instance).start();
        }
        return instance;
    }

    private Double latestCpuLoad = -1.0;
    private Double latestRamUsed = -1.0;

    public Map<String, Double> currentLoad() {
        Map<String, Double> load = new HashMap<String, Double>();
        load.put("cpuLoad", latestCpuLoad);
        load.put("ramUsed", latestRamUsed);
        return load;
    }

    public void processTopResult(String topResult) {
        try {
            Matcher cpuMatcher = Pattern.compile("%Cpu\\(s\\): +(\\d+\\.\\d+) us, +(\\d+\\.\\d+)").matcher(topResult);
            cpuMatcher.find();
            cpuMatcher.find();
            Double latestUserCpuLoad = Double.parseDouble(cpuMatcher.group(1));
            Double latestSystemCpuLoad = Double.parseDouble(cpuMatcher.group(2));
            latestCpuLoad = latestUserCpuLoad + latestSystemCpuLoad;

            Matcher ramMatcher = Pattern.compile("KiB Mem: +(\\d+) total, +(\\d+)").matcher(topResult);
            ramMatcher.find();
            latestRamUsed = Double.parseDouble(ramMatcher.group(2));
        } catch (Exception e) {
            System.out.println("Error #4 " + e);
        }
    }

    public void run() {
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();

        Runnable periodicTask = new Runnable() {
            public void run() {
                try {
                    Process getResourceUtil = Runtime.getRuntime().exec("top -bn2");
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(getResourceUtil.getInputStream()));

                    StringBuilder topResult = new StringBuilder();
                    String line;
                    while ( (line = reader.readLine()) != null) {
                        topResult.append(line);
                    }

                    processTopResult(topResult.toString());
                    reader.close();
                    getResourceUtil.destroy();
                } catch (IOException e) {
                    System.err.println("Error #2: " + e);
                }
            }
        };

        if (!System.getProperty("os.name").toLowerCase().contains("windows"))
            executor.scheduleAtFixedRate(periodicTask, 0, 5, TimeUnit.SECONDS);
    }
}

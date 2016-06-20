package com.routeHelpers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routeHelpers.dataTypes.BenchmarkInput;
import com.routeHelpers.dataTypes.BenchmarkOutput;
import com.routeHelpers.dataTypes.OutputData;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a623557 on 25-5-2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteRequest {
    Socket clientSocket;
    String unitName;

    public RouteRequest(String json, String unitName, Socket clientSocket, String output) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            this.clientSocket = clientSocket;
            this.unitName = unitName;

            Long t1 = time();
            BenchmarkInput benchmarkInput = mapper.readValue(json, BenchmarkInput.class);

            if (benchmarkInput.isForceException()) throw new Exception("Forced Exception");

            Long t2 = time();
            Map<String, Double> currentLoad = Monitor.getInstance().currentLoad();

            Long t3 = time();
            String response = mapper.writeValueAsString(new OutputData(output, benchmarkInput,
                    new BenchmarkOutput(currentLoad, fillAndCreateUnitPerformance(t1, t2, t3))));

            respond("200", response);

        } catch(Exception e) {
            System.err.println("Route request error: " + e);
            respond("500", "Internal server error");
        }
    }

    private Long time() { return System.currentTimeMillis(); }

    private void respond(String statusCode, String response) throws IOException {
        clientSocket.getOutputStream().write(("HTTP/1.1 "+statusCode+" OK\r\n\r\n" + response).getBytes("UTF-8"));
    }

    public Map<String, Long> fillAndCreateUnitPerformance(Long t1, Long t2, Long t3) {
        ArrayList<Long> unitPerformance = new ArrayList<Long>();
        unitPerformance.add(t1);
        unitPerformance.add(t2);
        unitPerformance.add(t3);
        unitPerformance.add(time());

        return createUnitPerformance(unitPerformance);
    }

    public Map<String, Long> createUnitPerformance(List<Long> list) {
        Map<String, Long> unitPerformance = new HashMap<String, Long>();
        for (int i =0; i<list.size()-1; i++) {
            unitPerformance.put(unitName + "_Unit"+i, list.get(i+1) - list.get(i));
        }
        return unitPerformance;
    }
}
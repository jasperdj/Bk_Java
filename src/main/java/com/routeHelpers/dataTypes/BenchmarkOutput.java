package com.routeHelpers.dataTypes;

import java.util.Map;

public class BenchmarkOutput {
    public Map<String, Double> resourceUtil;
    public Map<String, Long> unitPerformance;

    public BenchmarkOutput(Map<String, Double> resourceUtil, Map<String, Long> unitPerformance) {
        this.resourceUtil = resourceUtil;
        this.unitPerformance = unitPerformance;
    }

    public Map<String, Double> getResourceUtil() {
        return resourceUtil;
    }
    public void setResourceUtil(Map<String, Double> resourceUtil) {
        this.resourceUtil = resourceUtil;
    }
    public Map<String, Long> getUnitPerformance() {
        return unitPerformance;
    }
    public void setUnitPerformance(Map<String, Long> unitPerformance) {
        this.unitPerformance = unitPerformance;
    }
}
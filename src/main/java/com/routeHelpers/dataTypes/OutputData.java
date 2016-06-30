package com.routeHelpers.dataTypes;

public class OutputData {
    public BenchmarkInput benchmarkInput;
    public BenchmarkOutput benchmarkOutput;
    public String output;

    public OutputData(String output, BenchmarkInput benchmarkInput, BenchmarkOutput benchmarkOutput) {
        this.output = output;
        this.benchmarkInput = benchmarkInput;
        this.benchmarkOutput = benchmarkOutput;
    }

    public BenchmarkInput getBenchmarkInput() {
        return benchmarkInput;
    }
    public void setBenchmarkInput(BenchmarkInput benchmarkInput) {
        this.benchmarkInput = benchmarkInput;
    }
    public BenchmarkOutput getBenchmarkOutput() {
        return benchmarkOutput;
    }
    public void setBenchmarkOutput(BenchmarkOutput benchmarkOutput) {
        this.benchmarkOutput = benchmarkOutput;
    }
}
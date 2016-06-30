package com.routeHelpers.dataTypes;

public class BenchmarkInput {
    private int nodeId;
    private boolean forceException;

    public int getNodeId() {
        return nodeId;
    }
    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }
    public boolean isForceException() {
        return forceException;
    }
    public void setForceException(boolean forceException) {
        this.forceException = forceException;
    }
}
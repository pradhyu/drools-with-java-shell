package com.dmv.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SimulationResult {
    
    private LoadTestConfig config;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long totalExecutionTimeMs;
    private int totalExecutions;
    private int successfulExecutions;
    private int failedExecutions;
    private double averageResponseTimeMs;
    private double throughputPerSecond;
    private String errorMessage;
    private Map<String, Object> performanceMetrics = new HashMap<>();

    // Default constructor
    public SimulationResult() {}

    // Getters and Setters
    public LoadTestConfig getConfig() {
        return config;
    }

    public void setConfig(LoadTestConfig config) {
        this.config = config;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public long getTotalExecutionTimeMs() {
        return totalExecutionTimeMs;
    }

    public void setTotalExecutionTimeMs(long totalExecutionTimeMs) {
        this.totalExecutionTimeMs = totalExecutionTimeMs;
    }

    public int getTotalExecutions() {
        return totalExecutions;
    }

    public void setTotalExecutions(int totalExecutions) {
        this.totalExecutions = totalExecutions;
    }

    public int getSuccessfulExecutions() {
        return successfulExecutions;
    }

    public void setSuccessfulExecutions(int successfulExecutions) {
        this.successfulExecutions = successfulExecutions;
    }

    public int getFailedExecutions() {
        return failedExecutions;
    }

    public void setFailedExecutions(int failedExecutions) {
        this.failedExecutions = failedExecutions;
    }

    public double getAverageResponseTimeMs() {
        return averageResponseTimeMs;
    }

    public void setAverageResponseTimeMs(double averageResponseTimeMs) {
        this.averageResponseTimeMs = averageResponseTimeMs;
    }

    public double getThroughputPerSecond() {
        return throughputPerSecond;
    }

    public void setThroughputPerSecond(double throughputPerSecond) {
        this.throughputPerSecond = throughputPerSecond;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Object> getPerformanceMetrics() {
        return performanceMetrics;
    }

    public void setPerformanceMetrics(Map<String, Object> performanceMetrics) {
        this.performanceMetrics = performanceMetrics != null ? performanceMetrics : new HashMap<>();
    }

    // Helper methods
    public void addPerformanceMetric(String key, Object value) {
        if (performanceMetrics == null) {
            performanceMetrics = new HashMap<>();
        }
        performanceMetrics.put(key, value);
    }

    public double getSuccessRate() {
        return totalExecutions > 0 ? (double) successfulExecutions / totalExecutions * 100 : 0;
    }

    public double getFailureRate() {
        return totalExecutions > 0 ? (double) failedExecutions / totalExecutions * 100 : 0;
    }

    @Override
    public String toString() {
        return "SimulationResult{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", totalExecutionTimeMs=" + totalExecutionTimeMs +
                ", totalExecutions=" + totalExecutions +
                ", successfulExecutions=" + successfulExecutions +
                ", failedExecutions=" + failedExecutions +
                ", averageResponseTimeMs=" + String.format("%.2f", averageResponseTimeMs) +
                ", throughputPerSecond=" + String.format("%.2f", throughputPerSecond) +
                ", successRate=" + String.format("%.2f", getSuccessRate()) + "%" +
                '}';
    }
}
package com.dmv.model;

import java.time.LocalDateTime;

public class TestSummary {
    
    private int totalTests;
    private int passedTests;
    private int failedTests;
    private int skippedTests;
    private long totalExecutionTimeMs;
    private double successRate;
    private LocalDateTime executionTime;

    // Default constructor
    public TestSummary() {
        this.executionTime = LocalDateTime.now();
    }

    // Constructor
    public TestSummary(int totalTests, int passedTests, int failedTests) {
        this.totalTests = totalTests;
        this.passedTests = passedTests;
        this.failedTests = failedTests;
        this.executionTime = LocalDateTime.now();
        updateSuccessRate();
    }

    // Getters and Setters
    public int getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
        updateSuccessRate();
    }

    public int getPassedTests() {
        return passedTests;
    }

    public void setPassedTests(int passedTests) {
        this.passedTests = passedTests;
        updateSuccessRate();
    }

    public int getFailedTests() {
        return failedTests;
    }

    public void setFailedTests(int failedTests) {
        this.failedTests = failedTests;
        updateSuccessRate();
    }

    public int getSkippedTests() {
        return skippedTests;
    }

    public void setSkippedTests(int skippedTests) {
        this.skippedTests = skippedTests;
    }

    public long getTotalExecutionTimeMs() {
        return totalExecutionTimeMs;
    }

    public void setTotalExecutionTimeMs(long totalExecutionTimeMs) {
        this.totalExecutionTimeMs = totalExecutionTimeMs;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    // Helper methods
    private void updateSuccessRate() {
        this.successRate = totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
    }

    @Override
    public String toString() {
        return "TestSummary{" +
                "totalTests=" + totalTests +
                ", passedTests=" + passedTests +
                ", failedTests=" + failedTests +
                ", skippedTests=" + skippedTests +
                ", successRate=" + String.format("%.2f", successRate) + "%" +
                ", totalExecutionTimeMs=" + totalExecutionTimeMs +
                ", executionTime=" + executionTime +
                '}';
    }
}
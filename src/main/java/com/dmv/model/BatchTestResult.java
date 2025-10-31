package com.dmv.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class BatchTestResult {
    
    private String batchName;
    private LocalDateTime executionTime;
    private long totalExecutionTimeMs;
    private List<TestResult> testResults = new ArrayList<>();
    private BatchStatistics statistics;
    private Map<String, Object> aggregatedMetrics = new HashMap<>();

    // Default constructor
    public BatchTestResult() {
        this.executionTime = LocalDateTime.now();
    }

    // Constructor
    public BatchTestResult(String batchName) {
        this();
        this.batchName = batchName;
    }

    // Getters and Setters
    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    public long getTotalExecutionTimeMs() {
        return totalExecutionTimeMs;
    }

    public void setTotalExecutionTimeMs(long totalExecutionTimeMs) {
        this.totalExecutionTimeMs = totalExecutionTimeMs;
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults != null ? testResults : new ArrayList<>();
        calculateStatistics();
    }

    public BatchStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(BatchStatistics statistics) {
        this.statistics = statistics;
    }

    public Map<String, Object> getAggregatedMetrics() {
        return aggregatedMetrics;
    }

    public void setAggregatedMetrics(Map<String, Object> aggregatedMetrics) {
        this.aggregatedMetrics = aggregatedMetrics != null ? aggregatedMetrics : new HashMap<>();
    }

    // Helper methods
    public void addTestResult(TestResult result) {
        if (testResults == null) {
            testResults = new ArrayList<>();
        }
        testResults.add(result);
        calculateStatistics();
    }

    public void addAggregatedMetric(String key, Object value) {
        if (aggregatedMetrics == null) {
            aggregatedMetrics = new HashMap<>();
        }
        aggregatedMetrics.put(key, value);
    }

    private void calculateStatistics() {
        if (testResults == null || testResults.isEmpty()) {
            statistics = new BatchStatistics();
            return;
        }

        int totalTests = testResults.size();
        int passedTests = (int) testResults.stream().filter(TestResult::isSuccess).count();
        int failedTests = totalTests - passedTests;
        
        long totalOutcomes = testResults.stream().mapToLong(r -> r.getOutcomeResults().size()).sum();
        long passedOutcomes = testResults.stream().mapToLong(TestResult::getPassedOutcomes).sum();
        long failedOutcomes = testResults.stream().mapToLong(TestResult::getFailedOutcomes).sum();
        
        double averageExecutionTime = testResults.stream()
            .mapToLong(TestResult::getExecutionDurationMs)
            .average()
            .orElse(0.0);

        statistics = new BatchStatistics(
            totalTests, passedTests, failedTests,
            (int) totalOutcomes, (int) passedOutcomes, (int) failedOutcomes,
            averageExecutionTime
        );
    }

    public static class BatchStatistics {
        private int totalTests;
        private int passedTests;
        private int failedTests;
        private double successRate;
        private int totalOutcomes;
        private int passedOutcomes;
        private int failedOutcomes;
        private double outcomeSuccessRate;
        private double averageExecutionTimeMs;

        public BatchStatistics() {}

        public BatchStatistics(int totalTests, int passedTests, int failedTests,
                             int totalOutcomes, int passedOutcomes, int failedOutcomes,
                             double averageExecutionTimeMs) {
            this.totalTests = totalTests;
            this.passedTests = passedTests;
            this.failedTests = failedTests;
            this.successRate = totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
            this.totalOutcomes = totalOutcomes;
            this.passedOutcomes = passedOutcomes;
            this.failedOutcomes = failedOutcomes;
            this.outcomeSuccessRate = totalOutcomes > 0 ? (double) passedOutcomes / totalOutcomes * 100 : 0;
            this.averageExecutionTimeMs = averageExecutionTimeMs;
        }

        // Getters and Setters
        public int getTotalTests() {
            return totalTests;
        }

        public void setTotalTests(int totalTests) {
            this.totalTests = totalTests;
        }

        public int getPassedTests() {
            return passedTests;
        }

        public void setPassedTests(int passedTests) {
            this.passedTests = passedTests;
        }

        public int getFailedTests() {
            return failedTests;
        }

        public void setFailedTests(int failedTests) {
            this.failedTests = failedTests;
        }

        public double getSuccessRate() {
            return successRate;
        }

        public void setSuccessRate(double successRate) {
            this.successRate = successRate;
        }

        public int getTotalOutcomes() {
            return totalOutcomes;
        }

        public void setTotalOutcomes(int totalOutcomes) {
            this.totalOutcomes = totalOutcomes;
        }

        public int getPassedOutcomes() {
            return passedOutcomes;
        }

        public void setPassedOutcomes(int passedOutcomes) {
            this.passedOutcomes = passedOutcomes;
        }

        public int getFailedOutcomes() {
            return failedOutcomes;
        }

        public void setFailedOutcomes(int failedOutcomes) {
            this.failedOutcomes = failedOutcomes;
        }

        public double getOutcomeSuccessRate() {
            return outcomeSuccessRate;
        }

        public void setOutcomeSuccessRate(double outcomeSuccessRate) {
            this.outcomeSuccessRate = outcomeSuccessRate;
        }

        public double getAverageExecutionTimeMs() {
            return averageExecutionTimeMs;
        }

        public void setAverageExecutionTimeMs(double averageExecutionTimeMs) {
            this.averageExecutionTimeMs = averageExecutionTimeMs;
        }

        @Override
        public String toString() {
            return "BatchStatistics{" +
                    "totalTests=" + totalTests +
                    ", passedTests=" + passedTests +
                    ", failedTests=" + failedTests +
                    ", successRate=" + String.format("%.2f", successRate) + "%" +
                    ", totalOutcomes=" + totalOutcomes +
                    ", passedOutcomes=" + passedOutcomes +
                    ", failedOutcomes=" + failedOutcomes +
                    ", outcomeSuccessRate=" + String.format("%.2f", outcomeSuccessRate) + "%" +
                    ", averageExecutionTimeMs=" + String.format("%.2f", averageExecutionTimeMs) +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BatchTestResult{" +
                "batchName='" + batchName + '\'' +
                ", executionTime=" + executionTime +
                ", totalExecutionTimeMs=" + totalExecutionTimeMs +
                ", testResults=" + testResults.size() +
                ", statistics=" + statistics +
                '}';
    }
}
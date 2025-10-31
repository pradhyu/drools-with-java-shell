package com.dmv.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestReport {
    
    private LocalDateTime generatedAt;
    private List<TestResult> testResults = new ArrayList<>();
    private TestSummary summary;
    private String reportName;

    // Default constructor
    public TestReport() {
        this.generatedAt = LocalDateTime.now();
    }

    // Constructor
    public TestReport(String reportName) {
        this();
        this.reportName = reportName;
    }

    // Getters and Setters
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults != null ? testResults : new ArrayList<>();
    }

    public TestSummary getSummary() {
        return summary;
    }

    public void setSummary(TestSummary summary) {
        this.summary = summary;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    // Helper methods
    public void addTestResult(TestResult result) {
        if (testResults == null) {
            testResults = new ArrayList<>();
        }
        testResults.add(result);
    }

    public static class TestSummary {
        private int totalTests;
        private int passedTests;
        private int failedTests;
        private double successRate;
        private double averageExecutionTimeMs;

        public TestSummary() {}

        public TestSummary(int totalTests, int passedTests, int failedTests, double averageExecutionTimeMs) {
            this.totalTests = totalTests;
            this.passedTests = passedTests;
            this.failedTests = failedTests;
            this.successRate = totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
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

        public double getAverageExecutionTimeMs() {
            return averageExecutionTimeMs;
        }

        public void setAverageExecutionTimeMs(double averageExecutionTimeMs) {
            this.averageExecutionTimeMs = averageExecutionTimeMs;
        }

        @Override
        public String toString() {
            return "TestSummary{" +
                    "totalTests=" + totalTests +
                    ", passedTests=" + passedTests +
                    ", failedTests=" + failedTests +
                    ", successRate=" + String.format("%.2f", successRate) + "%" +
                    ", averageExecutionTimeMs=" + String.format("%.2f", averageExecutionTimeMs) +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TestReport{" +
                "generatedAt=" + generatedAt +
                ", reportName='" + reportName + '\'' +
                ", testResults=" + testResults.size() +
                ", summary=" + summary +
                '}';
    }
}
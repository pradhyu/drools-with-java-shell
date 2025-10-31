package com.dmv.model;

public class LoadTestConfig {
    
    private int concurrentUsers;
    private int durationSeconds;
    private int rampUpSeconds;
    private int rampDownSeconds;
    private String testScenario;
    private int requestsPerSecond;

    // Default constructor
    public LoadTestConfig() {
        this.concurrentUsers = 1;
        this.durationSeconds = 60;
        this.rampUpSeconds = 10;
        this.rampDownSeconds = 10;
    }

    // Constructor
    public LoadTestConfig(int concurrentUsers, int durationSeconds) {
        this.concurrentUsers = concurrentUsers;
        this.durationSeconds = durationSeconds;
        this.rampUpSeconds = 10;
        this.rampDownSeconds = 10;
    }

    // Getters and Setters
    public int getConcurrentUsers() {
        return concurrentUsers;
    }

    public void setConcurrentUsers(int concurrentUsers) {
        this.concurrentUsers = concurrentUsers;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getRampUpSeconds() {
        return rampUpSeconds;
    }

    public void setRampUpSeconds(int rampUpSeconds) {
        this.rampUpSeconds = rampUpSeconds;
    }

    public int getRampDownSeconds() {
        return rampDownSeconds;
    }

    public void setRampDownSeconds(int rampDownSeconds) {
        this.rampDownSeconds = rampDownSeconds;
    }

    public String getTestScenario() {
        return testScenario;
    }

    public void setTestScenario(String testScenario) {
        this.testScenario = testScenario;
    }

    public int getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public void setRequestsPerSecond(int requestsPerSecond) {
        this.requestsPerSecond = requestsPerSecond;
    }

    public int getExecutionsPerUser() {
        return requestsPerSecond > 0 ? requestsPerSecond : 1;
    }

    public TestScenario getTestScenarioObject() {
        // Return a default test scenario if none specified
        if (testScenario == null || testScenario.isEmpty()) {
            return new TestScenario("default-load-test", "Default load test scenario");
        }
        return new TestScenario(testScenario, "Load test scenario: " + testScenario);
    }

    @Override
    public String toString() {
        return "LoadTestConfig{" +
                "concurrentUsers=" + concurrentUsers +
                ", durationSeconds=" + durationSeconds +
                ", rampUpSeconds=" + rampUpSeconds +
                ", rampDownSeconds=" + rampDownSeconds +
                ", testScenario='" + testScenario + '\'' +
                ", requestsPerSecond=" + requestsPerSecond +
                '}';
    }
}
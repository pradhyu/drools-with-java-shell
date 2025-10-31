package com.dmv.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TestResult {
    
    private String scenarioName;
    private boolean success;
    private LocalDateTime executionTime;
    private long executionDurationMs;
    private List<OutcomeResult> outcomeResults = new ArrayList<>();
    private List<String> rulesFired = new ArrayList<>();
    private List<Object> resultingFacts = new ArrayList<>();
    private String errorMessage;
    private Map<String, Object> metrics = new HashMap<>();

    // Default constructor
    public TestResult() {
        this.executionTime = LocalDateTime.now();
    }

    // Constructor
    public TestResult(String scenarioName) {
        this();
        this.scenarioName = scenarioName;
    }

    // Getters and Setters
    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    public long getExecutionDurationMs() {
        return executionDurationMs;
    }

    public void setExecutionDurationMs(long executionDurationMs) {
        this.executionDurationMs = executionDurationMs;
    }

    public List<OutcomeResult> getOutcomeResults() {
        return outcomeResults;
    }

    public void setOutcomeResults(List<OutcomeResult> outcomeResults) {
        this.outcomeResults = outcomeResults != null ? outcomeResults : new ArrayList<>();
    }

    public List<String> getRulesFired() {
        return rulesFired;
    }

    public void setRulesFired(List<String> rulesFired) {
        this.rulesFired = rulesFired != null ? rulesFired : new ArrayList<>();
    }

    public List<Object> getResultingFacts() {
        return resultingFacts;
    }

    public void setResultingFacts(List<Object> resultingFacts) {
        this.resultingFacts = resultingFacts != null ? resultingFacts : new ArrayList<>();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics != null ? metrics : new HashMap<>();
    }

    // Helper methods
    public void addOutcomeResult(OutcomeResult result) {
        if (outcomeResults == null) {
            outcomeResults = new ArrayList<>();
        }
        outcomeResults.add(result);
    }

    public void addRuleFired(String ruleName) {
        if (rulesFired == null) {
            rulesFired = new ArrayList<>();
        }
        rulesFired.add(ruleName);
    }

    public void addResultingFact(Object fact) {
        if (resultingFacts == null) {
            resultingFacts = new ArrayList<>();
        }
        resultingFacts.add(fact);
    }

    public void addMetric(String key, Object value) {
        if (metrics == null) {
            metrics = new HashMap<>();
        }
        metrics.put(key, value);
    }

    public int getPassedOutcomes() {
        return (int) outcomeResults.stream().filter(OutcomeResult::isPassed).count();
    }

    public int getFailedOutcomes() {
        return (int) outcomeResults.stream().filter(result -> !result.isPassed()).count();
    }

    public static class OutcomeResult {
        private ExpectedOutcome expectedOutcome;
        private boolean passed;
        private Object actualValue;
        private String message;

        public OutcomeResult() {}

        public OutcomeResult(ExpectedOutcome expectedOutcome, boolean passed, Object actualValue, String message) {
            this.expectedOutcome = expectedOutcome;
            this.passed = passed;
            this.actualValue = actualValue;
            this.message = message;
        }

        // Getters and Setters
        public ExpectedOutcome getExpectedOutcome() {
            return expectedOutcome;
        }

        public void setExpectedOutcome(ExpectedOutcome expectedOutcome) {
            this.expectedOutcome = expectedOutcome;
        }

        public boolean isPassed() {
            return passed;
        }

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public Object getActualValue() {
            return actualValue;
        }

        public void setActualValue(Object actualValue) {
            this.actualValue = actualValue;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "OutcomeResult{" +
                    "passed=" + passed +
                    ", actualValue=" + actualValue +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "scenarioName='" + scenarioName + '\'' +
                ", success=" + success +
                ", executionTime=" + executionTime +
                ", executionDurationMs=" + executionDurationMs +
                ", outcomeResults=" + outcomeResults.size() +
                ", rulesFired=" + rulesFired.size() +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
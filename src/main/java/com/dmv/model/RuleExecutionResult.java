package com.dmv.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RuleExecutionResult {
    
    private boolean success;
    private int rulesFired;
    private List<String> firedRuleNames = new ArrayList<>();
    private List<Object> modifiedFacts = new ArrayList<>();
    private Map<String, Object> executionMetrics = new HashMap<>();
    private long executionTimeMs;
    private String errorMessage;

    public RuleExecutionResult() {}

    public RuleExecutionResult(boolean success) {
        this.success = success;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getRulesFired() {
        return rulesFired;
    }

    public void setRulesFired(int rulesFired) {
        this.rulesFired = rulesFired;
    }

    public List<String> getFiredRuleNames() {
        return firedRuleNames;
    }

    public void setFiredRuleNames(List<String> firedRuleNames) {
        this.firedRuleNames = firedRuleNames != null ? firedRuleNames : new ArrayList<>();
    }

    public List<Object> getModifiedFacts() {
        return modifiedFacts;
    }

    public void setModifiedFacts(List<Object> modifiedFacts) {
        this.modifiedFacts = modifiedFacts != null ? modifiedFacts : new ArrayList<>();
    }

    public Map<String, Object> getExecutionMetrics() {
        return executionMetrics;
    }

    public void setExecutionMetrics(Map<String, Object> executionMetrics) {
        this.executionMetrics = executionMetrics != null ? executionMetrics : new HashMap<>();
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Helper methods
    public void addFiredRule(String ruleName) {
        if (firedRuleNames == null) {
            firedRuleNames = new ArrayList<>();
        }
        firedRuleNames.add(ruleName);
        rulesFired++;
    }

    public void addModifiedFact(Object fact) {
        if (modifiedFacts == null) {
            modifiedFacts = new ArrayList<>();
        }
        modifiedFacts.add(fact);
    }

    public void addMetric(String key, Object value) {
        if (executionMetrics == null) {
            executionMetrics = new HashMap<>();
        }
        executionMetrics.put(key, value);
    }

    @Override
    public String toString() {
        return "RuleExecutionResult{" +
                "success=" + success +
                ", rulesFired=" + rulesFired +
                ", firedRuleNames=" + firedRuleNames +
                ", executionTimeMs=" + executionTimeMs +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
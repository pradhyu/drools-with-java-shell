package com.dmv.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExecutionMetadata {
    
    private int rulesFired;
    private List<String> firedRuleNames = new ArrayList<>();
    private long executionTimeMs;
    private LocalDateTime executionTime;
    private String sessionId;

    // Default constructor
    public ExecutionMetadata() {
        this.executionTime = LocalDateTime.now();
    }

    // Getters and Setters
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

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    // Helper methods
    public void addFiredRule(String ruleName) {
        if (firedRuleNames == null) {
            firedRuleNames = new ArrayList<>();
        }
        firedRuleNames.add(ruleName);
        rulesFired = firedRuleNames.size();
    }

    @Override
    public String toString() {
        return "ExecutionMetadata{" +
                "rulesFired=" + rulesFired +
                ", firedRuleNames=" + firedRuleNames +
                ", executionTimeMs=" + executionTimeMs +
                ", executionTime=" + executionTime +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
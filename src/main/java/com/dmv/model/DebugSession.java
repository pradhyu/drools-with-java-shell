package com.dmv.model;

import org.kie.api.runtime.KieSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DebugSession {
    
    private String sessionId;
    private String sessionName;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    private boolean stepDebugging;
    private Set<String> breakpoints = new HashSet<>();
    private List<ExecutionTrace> executionHistory = new ArrayList<>();
    private KieSession kieSession;
    private boolean active;

    // Default constructor
    public DebugSession() {
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
        this.active = true;
    }

    // Constructor
    public DebugSession(String sessionId, String sessionName) {
        this();
        this.sessionId = sessionId;
        this.sessionName = sessionName;
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public boolean isStepDebugging() {
        return stepDebugging;
    }

    public void setStepDebugging(boolean stepDebugging) {
        this.stepDebugging = stepDebugging;
    }

    public Set<String> getBreakpoints() {
        return breakpoints;
    }

    public void setBreakpoints(Set<String> breakpoints) {
        this.breakpoints = breakpoints != null ? breakpoints : new HashSet<>();
    }

    public List<ExecutionTrace> getExecutionHistory() {
        return executionHistory;
    }

    public void setExecutionHistory(List<ExecutionTrace> executionHistory) {
        this.executionHistory = executionHistory != null ? executionHistory : new ArrayList<>();
    }

    public KieSession getKieSession() {
        return kieSession;
    }

    public void setKieSession(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Helper methods
    public void addBreakpoint(String ruleName) {
        if (breakpoints == null) {
            breakpoints = new HashSet<>();
        }
        breakpoints.add(ruleName);
        updateLastAccessed();
    }

    public void removeBreakpoint(String ruleName) {
        if (breakpoints != null) {
            breakpoints.remove(ruleName);
        }
        updateLastAccessed();
    }

    public boolean hasBreakpoint(String ruleName) {
        return breakpoints != null && breakpoints.contains(ruleName);
    }

    public void addExecutionTrace(ExecutionTrace trace) {
        if (executionHistory == null) {
            executionHistory = new ArrayList<>();
        }
        executionHistory.add(trace);
        updateLastAccessed();
    }

    public void updateLastAccessed() {
        this.lastAccessedAt = LocalDateTime.now();
    }

    public void close() {
        if (kieSession != null) {
            kieSession.dispose();
        }
        this.active = false;
    }

    @Override
    public String toString() {
        return "DebugSession{" +
                "sessionId='" + sessionId + '\'' +
                ", sessionName='" + sessionName + '\'' +
                ", createdAt=" + createdAt +
                ", stepDebugging=" + stepDebugging +
                ", breakpoints=" + breakpoints.size() +
                ", executionHistory=" + executionHistory.size() +
                ", active=" + active +
                '}';
    }
}
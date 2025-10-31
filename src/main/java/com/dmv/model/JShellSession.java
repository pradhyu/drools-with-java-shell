package com.dmv.model;

import jdk.jshell.JShell;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JShellSession {
    
    private String sessionId;
    private JShell jshell;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    private List<String> executionHistory = new ArrayList<>();
    private boolean active;

    public JShellSession() {}

    public JShellSession(String sessionId, JShell jshell) {
        this.sessionId = sessionId;
        this.jshell = jshell;
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
        this.active = true;
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public JShell getJshell() {
        return jshell;
    }

    public void setJshell(JShell jshell) {
        this.jshell = jshell;
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

    public List<String> getExecutionHistory() {
        return executionHistory;
    }

    public void setExecutionHistory(List<String> executionHistory) {
        this.executionHistory = executionHistory != null ? executionHistory : new ArrayList<>();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Helper methods
    public void addToHistory(String code) {
        if (executionHistory == null) {
            executionHistory = new ArrayList<>();
        }
        executionHistory.add(code);
        updateLastAccessed();
    }

    public void updateLastAccessed() {
        this.lastAccessedAt = LocalDateTime.now();
    }

    public void close() {
        if (jshell != null) {
            jshell.close();
        }
        this.active = false;
    }

    @Override
    public String toString() {
        return "JShellSession{" +
                "sessionId='" + sessionId + '\'' +
                ", createdAt=" + createdAt +
                ", lastAccessedAt=" + lastAccessedAt +
                ", active=" + active +
                ", historySize=" + (executionHistory != null ? executionHistory.size() : 0) +
                '}';
    }
}
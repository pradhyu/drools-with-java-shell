package com.dmv.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionTrace {
    
    private String traceId;
    private LocalDateTime timestamp;
    private List<RuleFiring> ruleFirings = new ArrayList<>();
    private List<FactModification> factModifications = new ArrayList<>();
    private Map<String, Object> performanceMetrics = new HashMap<>();
    private long executionTimeMs;
    private List<String> agenda = new ArrayList<>();
    private int totalRulesFired;

    // Default constructor
    public ExecutionTrace() {
        this.timestamp = LocalDateTime.now();
        this.traceId = "trace-" + System.currentTimeMillis();
    }

    // Constructor
    public ExecutionTrace(String traceId) {
        this();
        this.traceId = traceId;
    }

    // Getters and Setters
    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<RuleFiring> getRuleFirings() {
        return ruleFirings;
    }

    public void setRuleFirings(List<RuleFiring> ruleFirings) {
        this.ruleFirings = ruleFirings != null ? ruleFirings : new ArrayList<>();
    }

    public List<FactModification> getFactModifications() {
        return factModifications;
    }

    public void setFactModifications(List<FactModification> factModifications) {
        this.factModifications = factModifications != null ? factModifications : new ArrayList<>();
    }

    public Map<String, Object> getPerformanceMetrics() {
        return performanceMetrics;
    }

    public void setPerformanceMetrics(Map<String, Object> performanceMetrics) {
        this.performanceMetrics = performanceMetrics != null ? performanceMetrics : new HashMap<>();
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public List<String> getAgenda() {
        return agenda;
    }

    public void setAgenda(List<String> agenda) {
        this.agenda = agenda != null ? agenda : new ArrayList<>();
    }

    public int getTotalRulesFired() {
        return totalRulesFired;
    }

    public void setTotalRulesFired(int totalRulesFired) {
        this.totalRulesFired = totalRulesFired;
    }

    // Helper methods
    public void addRuleFiring(RuleFiring firing) {
        if (ruleFirings == null) {
            ruleFirings = new ArrayList<>();
        }
        ruleFirings.add(firing);
        totalRulesFired++;
    }

    public void addFactModification(FactModification modification) {
        if (factModifications == null) {
            factModifications = new ArrayList<>();
        }
        factModifications.add(modification);
    }

    public void addPerformanceMetric(String key, Object value) {
        if (performanceMetrics == null) {
            performanceMetrics = new HashMap<>();
        }
        performanceMetrics.put(key, value);
    }

    public static class RuleFiring {
        private String ruleName;
        private LocalDateTime firingTime;
        private List<Object> matchedFacts = new ArrayList<>();
        private long executionTimeMs;
        private int salience;

        public RuleFiring() {
            this.firingTime = LocalDateTime.now();
        }

        public RuleFiring(String ruleName) {
            this();
            this.ruleName = ruleName;
        }

        // Getters and Setters
        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        public LocalDateTime getFiringTime() {
            return firingTime;
        }

        public void setFiringTime(LocalDateTime firingTime) {
            this.firingTime = firingTime;
        }

        public List<Object> getMatchedFacts() {
            return matchedFacts;
        }

        public void setMatchedFacts(List<Object> matchedFacts) {
            this.matchedFacts = matchedFacts != null ? matchedFacts : new ArrayList<>();
        }

        public long getExecutionTimeMs() {
            return executionTimeMs;
        }

        public void setExecutionTimeMs(long executionTimeMs) {
            this.executionTimeMs = executionTimeMs;
        }

        public int getSalience() {
            return salience;
        }

        public void setSalience(int salience) {
            this.salience = salience;
        }

        public void addMatchedFact(Object fact) {
            if (matchedFacts == null) {
                matchedFacts = new ArrayList<>();
            }
            matchedFacts.add(fact);
        }

        @Override
        public String toString() {
            return "RuleFiring{" +
                    "ruleName='" + ruleName + '\'' +
                    ", firingTime=" + firingTime +
                    ", matchedFacts=" + matchedFacts.size() +
                    ", executionTimeMs=" + executionTimeMs +
                    ", salience=" + salience +
                    '}';
        }
    }

    public static class FactModification {
        private ModificationType type;
        private Object fact;
        private String factType;
        private LocalDateTime modificationTime;
        private String ruleName;

        public FactModification() {
            this.modificationTime = LocalDateTime.now();
        }

        public FactModification(ModificationType type, Object fact, String ruleName) {
            this();
            this.type = type;
            this.fact = fact;
            this.factType = fact != null ? fact.getClass().getSimpleName() : null;
            this.ruleName = ruleName;
        }

        // Getters and Setters
        public ModificationType getType() {
            return type;
        }

        public void setType(ModificationType type) {
            this.type = type;
        }

        public Object getFact() {
            return fact;
        }

        public void setFact(Object fact) {
            this.fact = fact;
            this.factType = fact != null ? fact.getClass().getSimpleName() : null;
        }

        public String getFactType() {
            return factType;
        }

        public void setFactType(String factType) {
            this.factType = factType;
        }

        public LocalDateTime getModificationTime() {
            return modificationTime;
        }

        public void setModificationTime(LocalDateTime modificationTime) {
            this.modificationTime = modificationTime;
        }

        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        public enum ModificationType {
            INSERTED,
            UPDATED,
            RETRACTED
        }

        @Override
        public String toString() {
            return "FactModification{" +
                    "type=" + type +
                    ", factType='" + factType + '\'' +
                    ", modificationTime=" + modificationTime +
                    ", ruleName='" + ruleName + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ExecutionTrace{" +
                "traceId='" + traceId + '\'' +
                ", timestamp=" + timestamp +
                ", ruleFirings=" + ruleFirings.size() +
                ", factModifications=" + factModifications.size() +
                ", executionTimeMs=" + executionTimeMs +
                ", totalRulesFired=" + totalRulesFired +
                '}';
    }
}
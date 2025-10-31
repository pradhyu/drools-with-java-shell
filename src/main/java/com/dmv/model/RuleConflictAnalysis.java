package com.dmv.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RuleConflictAnalysis {
    
    private LocalDateTime analysisTime;
    private List<RuleConflict> conflicts = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();
    private ConflictSeverity overallSeverity;
    private int totalRulesAnalyzed;

    // Default constructor
    public RuleConflictAnalysis() {
        this.analysisTime = LocalDateTime.now();
        this.overallSeverity = ConflictSeverity.NONE;
    }

    // Getters and Setters
    public LocalDateTime getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(LocalDateTime analysisTime) {
        this.analysisTime = analysisTime;
    }

    public List<RuleConflict> getConflicts() {
        return conflicts;
    }

    public void setConflicts(List<RuleConflict> conflicts) {
        this.conflicts = conflicts != null ? conflicts : new ArrayList<>();
        updateOverallSeverity();
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
    }

    public ConflictSeverity getOverallSeverity() {
        return overallSeverity;
    }

    public void setOverallSeverity(ConflictSeverity overallSeverity) {
        this.overallSeverity = overallSeverity;
    }

    public int getTotalRulesAnalyzed() {
        return totalRulesAnalyzed;
    }

    public void setTotalRulesAnalyzed(int totalRulesAnalyzed) {
        this.totalRulesAnalyzed = totalRulesAnalyzed;
    }

    // Helper methods
    public void addConflict(RuleConflict conflict) {
        if (conflicts == null) {
            conflicts = new ArrayList<>();
        }
        conflicts.add(conflict);
        updateOverallSeverity();
    }

    public void addSuggestion(String suggestion) {
        if (suggestions == null) {
            suggestions = new ArrayList<>();
        }
        suggestions.add(suggestion);
    }

    public boolean hasConflicts() {
        return conflicts != null && !conflicts.isEmpty();
    }

    public int getConflictCount() {
        return conflicts != null ? conflicts.size() : 0;
    }

    private void updateOverallSeverity() {
        if (conflicts == null || conflicts.isEmpty()) {
            overallSeverity = ConflictSeverity.NONE;
            return;
        }

        ConflictSeverity maxSeverity = ConflictSeverity.NONE;
        for (RuleConflict conflict : conflicts) {
            if (conflict.getSeverity().ordinal() > maxSeverity.ordinal()) {
                maxSeverity = conflict.getSeverity();
            }
        }
        overallSeverity = maxSeverity;
    }

    public static class RuleConflict {
        private ConflictType type;
        private ConflictSeverity severity;
        private List<String> involvedRules = new ArrayList<>();
        private String description;
        private String recommendation;

        public RuleConflict() {}

        public RuleConflict(ConflictType type, ConflictSeverity severity, String description) {
            this.type = type;
            this.severity = severity;
            this.description = description;
        }

        // Getters and Setters
        public ConflictType getType() {
            return type;
        }

        public void setType(ConflictType type) {
            this.type = type;
        }

        public ConflictSeverity getSeverity() {
            return severity;
        }

        public void setSeverity(ConflictSeverity severity) {
            this.severity = severity;
        }

        public List<String> getInvolvedRules() {
            return involvedRules;
        }

        public void setInvolvedRules(List<String> involvedRules) {
            this.involvedRules = involvedRules != null ? involvedRules : new ArrayList<>();
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getRecommendation() {
            return recommendation;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }

        public void addInvolvedRule(String ruleName) {
            if (involvedRules == null) {
                involvedRules = new ArrayList<>();
            }
            involvedRules.add(ruleName);
        }

        @Override
        public String toString() {
            return "RuleConflict{" +
                    "type=" + type +
                    ", severity=" + severity +
                    ", involvedRules=" + involvedRules +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    public enum ConflictType {
        SALIENCE_CONFLICT,
        MUTUAL_EXCLUSION,
        CIRCULAR_DEPENDENCY,
        REDUNDANT_RULES,
        CONTRADICTORY_ACTIONS,
        PERFORMANCE_BOTTLENECK
    }

    public enum ConflictSeverity {
        NONE,
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    @Override
    public String toString() {
        return "RuleConflictAnalysis{" +
                "analysisTime=" + analysisTime +
                ", conflicts=" + conflicts.size() +
                ", overallSeverity=" + overallSeverity +
                ", totalRulesAnalyzed=" + totalRulesAnalyzed +
                '}';
    }
}
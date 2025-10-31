package com.dmv.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RuleDiff {
    
    private String ruleName;
    private String version1Id;
    private String version2Id;
    private LocalDateTime version1Timestamp;
    private LocalDateTime version2Timestamp;
    private String version1Author;
    private String version2Author;
    private List<DiffLine> diffLines = new ArrayList<>();
    private DiffStatistics statistics;

    public RuleDiff() {}

    public RuleDiff(String ruleName, String version1Id, String version2Id) {
        this.ruleName = ruleName;
        this.version1Id = version1Id;
        this.version2Id = version2Id;
    }

    // Getters and Setters
    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getVersion1Id() {
        return version1Id;
    }

    public void setVersion1Id(String version1Id) {
        this.version1Id = version1Id;
    }

    public String getVersion2Id() {
        return version2Id;
    }

    public void setVersion2Id(String version2Id) {
        this.version2Id = version2Id;
    }

    public LocalDateTime getVersion1Timestamp() {
        return version1Timestamp;
    }

    public void setVersion1Timestamp(LocalDateTime version1Timestamp) {
        this.version1Timestamp = version1Timestamp;
    }

    public LocalDateTime getVersion2Timestamp() {
        return version2Timestamp;
    }

    public void setVersion2Timestamp(LocalDateTime version2Timestamp) {
        this.version2Timestamp = version2Timestamp;
    }

    public String getVersion1Author() {
        return version1Author;
    }

    public void setVersion1Author(String version1Author) {
        this.version1Author = version1Author;
    }

    public String getVersion2Author() {
        return version2Author;
    }

    public void setVersion2Author(String version2Author) {
        this.version2Author = version2Author;
    }

    public List<DiffLine> getDiffLines() {
        return diffLines;
    }

    public void setDiffLines(List<DiffLine> diffLines) {
        this.diffLines = diffLines != null ? diffLines : new ArrayList<>();
    }

    public DiffStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(DiffStatistics statistics) {
        this.statistics = statistics;
    }

    // Helper methods
    public void addDiffLine(DiffLine diffLine) {
        if (diffLines == null) {
            diffLines = new ArrayList<>();
        }
        diffLines.add(diffLine);
    }

    public static class DiffLine {
        private DiffType type;
        private int lineNumber;
        private String content;

        public DiffLine() {}

        public DiffLine(DiffType type, int lineNumber, String content) {
            this.type = type;
            this.lineNumber = lineNumber;
            this.content = content;
        }

        // Getters and Setters
        public DiffType getType() {
            return type;
        }

        public void setType(DiffType type) {
            this.type = type;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "DiffLine{" +
                    "type=" + type +
                    ", lineNumber=" + lineNumber +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public static class DiffStatistics {
        private int linesAdded;
        private int linesRemoved;
        private int linesModified;
        private int totalChanges;

        public DiffStatistics() {}

        public DiffStatistics(int linesAdded, int linesRemoved, int linesModified) {
            this.linesAdded = linesAdded;
            this.linesRemoved = linesRemoved;
            this.linesModified = linesModified;
            this.totalChanges = linesAdded + linesRemoved + linesModified;
        }

        // Getters and Setters
        public int getLinesAdded() {
            return linesAdded;
        }

        public void setLinesAdded(int linesAdded) {
            this.linesAdded = linesAdded;
            updateTotalChanges();
        }

        public int getLinesRemoved() {
            return linesRemoved;
        }

        public void setLinesRemoved(int linesRemoved) {
            this.linesRemoved = linesRemoved;
            updateTotalChanges();
        }

        public int getLinesModified() {
            return linesModified;
        }

        public void setLinesModified(int linesModified) {
            this.linesModified = linesModified;
            updateTotalChanges();
        }

        public int getTotalChanges() {
            return totalChanges;
        }

        private void updateTotalChanges() {
            this.totalChanges = linesAdded + linesRemoved + linesModified;
        }

        @Override
        public String toString() {
            return "DiffStatistics{" +
                    "linesAdded=" + linesAdded +
                    ", linesRemoved=" + linesRemoved +
                    ", linesModified=" + linesModified +
                    ", totalChanges=" + totalChanges +
                    '}';
        }
    }

    public enum DiffType {
        ADDED,
        REMOVED,
        MODIFIED,
        UNCHANGED
    }

    @Override
    public String toString() {
        return "RuleDiff{" +
                "ruleName='" + ruleName + '\'' +
                ", version1Id='" + version1Id + '\'' +
                ", version2Id='" + version2Id + '\'' +
                ", diffLines=" + diffLines.size() +
                ", statistics=" + statistics +
                '}';
    }
}
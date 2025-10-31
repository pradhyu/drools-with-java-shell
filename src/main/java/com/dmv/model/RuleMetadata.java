package com.dmv.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RuleMetadata {
    
    private String ruleName;
    private String description;
    private LocalDateTime lastModified;
    private String author;
    private RuleStatus status;
    private List<String> tags = new ArrayList<>();
    private String packageName;
    private int priority;

    public RuleMetadata() {}

    public RuleMetadata(String ruleName, String description) {
        this.ruleName = ruleName;
        this.description = description;
        this.lastModified = LocalDateTime.now();
        this.status = RuleStatus.ACTIVE;
    }

    // Getters and Setters
    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public RuleStatus getStatus() {
        return status;
    }

    public void setStatus(RuleStatus status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    // Helper methods
    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
    }

    @Override
    public String toString() {
        return "RuleMetadata{" +
                "ruleName='" + ruleName + '\'' +
                ", description='" + description + '\'' +
                ", lastModified=" + lastModified +
                ", author='" + author + '\'' +
                ", status=" + status +
                ", tags=" + tags +
                ", packageName='" + packageName + '\'' +
                ", priority=" + priority +
                '}';
    }
}
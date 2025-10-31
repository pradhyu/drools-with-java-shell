package com.dmv.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rule_versions")
public class RuleVersion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "version_id", unique = true, nullable = false)
    private String versionId;
    
    @Column(name = "rule_name", nullable = false)
    private String ruleName;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "author")
    private String author;
    
    @Column(name = "commit_message")
    private String commitMessage;
    
    @ElementCollection
    @CollectionTable(name = "rule_version_tags", joinColumns = @JoinColumn(name = "rule_version_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    @Column(name = "is_active")
    private boolean active = true;

    // Default constructor
    public RuleVersion() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor
    public RuleVersion(String versionId, String ruleName, String content, String author) {
        this();
        this.versionId = versionId;
        this.ruleName = ruleName;
        this.content = content;
        this.author = author;
    }

    // Constructor with commit message
    public RuleVersion(String versionId, String ruleName, String content, String author, String commitMessage) {
        this(versionId, ruleName, content, author);
        this.commitMessage = commitMessage;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Helper methods
    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(String tag) {
        if (tags != null) {
            tags.remove(tag);
        }
    }

    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }

    @Override
    public String toString() {
        return "RuleVersion{" +
                "id=" + id +
                ", versionId='" + versionId + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", timestamp=" + timestamp +
                ", author='" + author + '\'' +
                ", commitMessage='" + commitMessage + '\'' +
                ", tags=" + tags +
                ", active=" + active +
                '}';
    }
}
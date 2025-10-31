package com.dmv.model;

public class CompletionSuggestion {
    
    private String suggestion;
    private String description;
    private String type;
    private int startPosition;
    private int endPosition;

    public CompletionSuggestion() {}

    public CompletionSuggestion(String suggestion, String description, String type) {
        this.suggestion = suggestion;
        this.description = description;
        this.type = type;
    }

    // Getters and Setters
    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    @Override
    public String toString() {
        return "CompletionSuggestion{" +
                "suggestion='" + suggestion + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
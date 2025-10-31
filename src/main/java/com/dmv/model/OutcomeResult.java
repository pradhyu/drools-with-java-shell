package com.dmv.model;

public class OutcomeResult {
    
    private String expected;
    private String actual;
    private boolean matched;
    private String description;
    private String errorMessage;

    // Default constructor
    public OutcomeResult() {}

    // Constructor
    public OutcomeResult(String expected, String actual) {
        this.expected = expected;
        this.actual = actual;
        this.matched = (expected != null && expected.equals(actual)) || 
                      (expected == null && actual == null);
    }

    // Getters and Setters
    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
        updateMatched();
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
        updateMatched();
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Helper methods
    private void updateMatched() {
        this.matched = (expected != null && expected.equals(actual)) || 
                      (expected == null && actual == null);
    }

    @Override
    public String toString() {
        return "OutcomeResult{" +
                "expected='" + expected + '\'' +
                ", actual='" + actual + '\'' +
                ", matched=" + matched +
                ", description='" + description + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
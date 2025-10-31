package com.dmv.model;

public enum ViolationStatus {
    OUTSTANDING("Outstanding"),
    RESOLVED("Resolved"),
    DISPUTED("Disputed"),
    WAIVED("Waived");

    private final String description;

    ViolationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
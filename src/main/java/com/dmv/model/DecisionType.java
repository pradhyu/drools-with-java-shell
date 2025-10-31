package com.dmv.model;

public enum DecisionType {
    APPROVED("Approved"),
    REJECTED("Rejected"),
    REQUIRES_ACTION("Requires Action");

    private final String description;

    DecisionType(String description) {
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
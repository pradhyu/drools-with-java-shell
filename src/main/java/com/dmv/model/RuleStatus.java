package com.dmv.model;

public enum RuleStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    DRAFT("Draft"),
    DEPRECATED("Deprecated"),
    ERROR("Error");

    private final String description;

    RuleStatus(String description) {
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
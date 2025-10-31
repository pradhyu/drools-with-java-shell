package com.dmv.model;

public enum RenewalType {
    STANDARD("Standard Renewal"),
    EXPEDITED("Expedited Renewal"),
    REPLACEMENT("Replacement License"),
    UPGRADE("License Upgrade"),
    REINSTATEMENT("License Reinstatement");

    private final String description;

    RenewalType(String description) {
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
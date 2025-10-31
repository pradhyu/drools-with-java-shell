package com.dmv.model;

public enum LicenseStatus {
    ACTIVE("Active"),
    EXPIRED("Expired"),
    SUSPENDED("Suspended"),
    REVOKED("Revoked"),
    PENDING("Pending");

    private final String description;

    LicenseStatus(String description) {
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
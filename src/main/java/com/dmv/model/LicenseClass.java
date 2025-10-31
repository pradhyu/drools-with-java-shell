package com.dmv.model;

public enum LicenseClass {
    CLASS_A("Class A - Commercial Driver's License"),
    CLASS_B("Class B - Commercial Driver's License"),
    CLASS_C("Class C - Regular Driver's License"),
    MOTORCYCLE("Motorcycle License"),
    LEARNER_PERMIT("Learner's Permit"),
    RESTRICTED("Restricted License");

    private final String description;

    LicenseClass(String description) {
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
package com.dmv.model;

public class ExpectedOutcome {
    
    private OutcomeType type;
    private String description;
    private Object expectedValue;
    private String factType;
    private String propertyPath;

    // Default constructor
    public ExpectedOutcome() {}

    // Constructor
    public ExpectedOutcome(OutcomeType type, String description, Object expectedValue) {
        this.type = type;
        this.description = description;
        this.expectedValue = expectedValue;
    }

    // Getters and Setters
    public OutcomeType getType() {
        return type;
    }

    public void setType(OutcomeType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(Object expectedValue) {
        this.expectedValue = expectedValue;
    }

    public String getFactType() {
        return factType;
    }

    public void setFactType(String factType) {
        this.factType = factType;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public enum OutcomeType {
        FACT_CREATED,
        FACT_MODIFIED,
        FACT_DELETED,
        PROPERTY_VALUE,
        RULE_FIRED,
        RULE_NOT_FIRED,
        DECISION_MADE,
        ERROR_OCCURRED
    }

    @Override
    public String toString() {
        return "ExpectedOutcome{" +
                "type=" + type +
                ", description='" + description + '\'' +
                ", expectedValue=" + expectedValue +
                ", factType='" + factType + '\'' +
                ", propertyPath='" + propertyPath + '\'' +
                '}';
    }
}
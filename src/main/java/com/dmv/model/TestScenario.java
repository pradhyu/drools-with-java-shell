package com.dmv.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TestScenario {
    
    @NotBlank(message = "Scenario name is required")
    private String scenarioName;
    
    private String description;
    
    @NotNull(message = "Input facts are required")
    private List<Object> inputFacts = new ArrayList<>();
    
    private List<ExpectedOutcome> expectedOutcomes = new ArrayList<>();
    
    private String category;
    private List<String> tags = new ArrayList<>();

    // Default constructor
    public TestScenario() {}

    // Constructor
    public TestScenario(String scenarioName, String description) {
        this.scenarioName = scenarioName;
        this.description = description;
    }

    // Getters and Setters
    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Object> getInputFacts() {
        return inputFacts;
    }

    public void setInputFacts(List<Object> inputFacts) {
        this.inputFacts = inputFacts != null ? inputFacts : new ArrayList<>();
    }

    public List<ExpectedOutcome> getExpectedOutcomes() {
        return expectedOutcomes;
    }

    public void setExpectedOutcomes(List<ExpectedOutcome> expectedOutcomes) {
        this.expectedOutcomes = expectedOutcomes != null ? expectedOutcomes : new ArrayList<>();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    // Helper methods
    public void addInputFact(Object fact) {
        if (inputFacts == null) {
            inputFacts = new ArrayList<>();
        }
        inputFacts.add(fact);
    }

    public void addExpectedOutcome(ExpectedOutcome outcome) {
        if (expectedOutcomes == null) {
            expectedOutcomes = new ArrayList<>();
        }
        expectedOutcomes.add(outcome);
    }

    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    @Override
    public String toString() {
        return "TestScenario{" +
                "scenarioName='" + scenarioName + '\'' +
                ", description='" + description + '\'' +
                ", inputFacts=" + inputFacts.size() +
                ", expectedOutcomes=" + expectedOutcomes.size() +
                ", category='" + category + '\'' +
                ", tags=" + tags +
                '}';
    }
}
package com.dmv.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RenewalDecision {
    
    @NotNull(message = "Decision type is required")
    private DecisionType decision;
    
    private List<String> requirements = new ArrayList<>();
    
    private List<String> reasons = new ArrayList<>();
    
    private BigDecimal fee;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validUntil;

    // Default constructor
    public RenewalDecision() {}

    // Constructor
    public RenewalDecision(DecisionType decision) {
        this.decision = decision;
    }

    // Getters and Setters
    public DecisionType getDecision() {
        return decision;
    }

    public void setDecision(DecisionType decision) {
        this.decision = decision;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = requirements != null ? requirements : new ArrayList<>();
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons != null ? reasons : new ArrayList<>();
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    // Helper methods
    public void addRequirement(String requirement) {
        if (requirements == null) {
            requirements = new ArrayList<>();
        }
        requirements.add(requirement);
    }

    public void addReason(String reason) {
        if (reasons == null) {
            reasons = new ArrayList<>();
        }
        reasons.add(reason);
    }

    @Override
    public String toString() {
        return "RenewalDecision{" +
                "decision=" + decision +
                ", requirements=" + requirements +
                ", reasons=" + reasons +
                ", fee=" + fee +
                ", validUntil=" + validUntil +
                '}';
    }
}
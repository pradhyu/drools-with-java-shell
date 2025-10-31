package com.dmv.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Violation {
    
    @NotBlank(message = "Violation code is required")
    private String violationCode;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Violation date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate violationDate;
    
    @NotNull(message = "Fine amount is required")
    private BigDecimal fineAmount;
    
    @NotNull(message = "Violation status is required")
    private ViolationStatus status;
    
    private LocalDate resolvedDate;

    // Default constructor
    public Violation() {}

    // Constructor
    public Violation(String violationCode, String description, LocalDate violationDate, 
                    BigDecimal fineAmount, ViolationStatus status) {
        this.violationCode = violationCode;
        this.description = description;
        this.violationDate = violationDate;
        this.fineAmount = fineAmount;
        this.status = status;
    }

    // Getters and Setters
    public String getViolationCode() {
        return violationCode;
    }

    public void setViolationCode(String violationCode) {
        this.violationCode = violationCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getViolationDate() {
        return violationDate;
    }

    public void setViolationDate(LocalDate violationDate) {
        this.violationDate = violationDate;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public ViolationStatus getStatus() {
        return status;
    }

    public void setStatus(ViolationStatus status) {
        this.status = status;
    }

    public LocalDate getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(LocalDate resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    // Helper methods
    public boolean isOutstanding() {
        return status == ViolationStatus.OUTSTANDING;
    }

    public boolean isResolved() {
        return status == ViolationStatus.RESOLVED;
    }

    @Override
    public String toString() {
        return "Violation{" +
                "violationCode='" + violationCode + '\'' +
                ", description='" + description + '\'' +
                ", violationDate=" + violationDate +
                ", fineAmount=" + fineAmount +
                ", status=" + status +
                ", resolvedDate=" + resolvedDate +
                '}';
    }
}
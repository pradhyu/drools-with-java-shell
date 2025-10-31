package com.dmv.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LicenseInfo {
    
    @NotBlank(message = "License number is required")
    private String licenseNumber;
    
    @NotNull(message = "License class is required")
    private LicenseClass licenseClass;
    
    @NotNull(message = "Issue date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;
    
    @NotNull(message = "Expiration date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;
    
    @NotNull(message = "License status is required")
    private LicenseStatus status;

    // Default constructor
    public LicenseInfo() {}

    // Constructor
    public LicenseInfo(String licenseNumber, LicenseClass licenseClass, LocalDate issueDate, 
                      LocalDate expirationDate, LicenseStatus status) {
        this.licenseNumber = licenseNumber;
        this.licenseClass = licenseClass;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.status = status;
    }

    // Getters and Setters
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LicenseClass getLicenseClass() {
        return licenseClass;
    }

    public void setLicenseClass(LicenseClass licenseClass) {
        this.licenseClass = licenseClass;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LicenseStatus getStatus() {
        return status;
    }

    public void setStatus(LicenseStatus status) {
        this.status = status;
    }

    // Helper methods
    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }

    public long getDaysUntilExpiration() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
    }

    public long getMonthsSinceExpiration() {
        if (!isExpired()) {
            return 0;
        }
        return ChronoUnit.MONTHS.between(expirationDate, LocalDate.now());
    }

    @Override
    public String toString() {
        return "LicenseInfo{" +
                "licenseNumber='" + licenseNumber + '\'' +
                ", licenseClass=" + licenseClass +
                ", issueDate=" + issueDate +
                ", expirationDate=" + expirationDate +
                ", status=" + status +
                '}';
    }
}
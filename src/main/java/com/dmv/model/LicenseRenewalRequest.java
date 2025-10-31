package com.dmv.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LicenseRenewalRequest {
    
    @NotBlank(message = "Applicant ID is required")
    private String applicantId;
    
    @NotNull(message = "Personal information is required")
    @Valid
    private PersonalInfo personalInfo;
    
    @NotNull(message = "Current license information is required")
    @Valid
    private LicenseInfo currentLicense;
    
    @Valid
    private List<Violation> violations = new ArrayList<>();
    
    @NotNull(message = "Renewal type is required")
    private RenewalType renewalType;

    // Default constructor
    public LicenseRenewalRequest() {}

    // Constructor
    public LicenseRenewalRequest(String applicantId, PersonalInfo personalInfo, 
                               LicenseInfo currentLicense, RenewalType renewalType) {
        this.applicantId = applicantId;
        this.personalInfo = personalInfo;
        this.currentLicense = currentLicense;
        this.renewalType = renewalType;
    }

    // Getters and Setters
    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    public LicenseInfo getCurrentLicense() {
        return currentLicense;
    }

    public void setCurrentLicense(LicenseInfo currentLicense) {
        this.currentLicense = currentLicense;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations != null ? violations : new ArrayList<>();
    }

    public RenewalType getRenewalType() {
        return renewalType;
    }

    public void setRenewalType(RenewalType renewalType) {
        this.renewalType = renewalType;
    }

    // Helper methods
    public boolean hasOutstandingViolations() {
        return violations.stream().anyMatch(Violation::isOutstanding);
    }

    public int getOutstandingViolationCount() {
        return (int) violations.stream().filter(Violation::isOutstanding).count();
    }

    @Override
    public String toString() {
        return "LicenseRenewalRequest{" +
                "applicantId='" + applicantId + '\'' +
                ", personalInfo=" + personalInfo +
                ", currentLicense=" + currentLicense +
                ", violations=" + violations +
                ", renewalType=" + renewalType +
                '}';
    }
}
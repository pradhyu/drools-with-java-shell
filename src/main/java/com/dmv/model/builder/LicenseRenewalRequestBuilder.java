package com.dmv.model.builder;

import com.dmv.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for creating sample LicenseRenewalRequest objects for testing
 */
public class LicenseRenewalRequestBuilder {
    
    private String applicantId;
    private PersonalInfo personalInfo;
    private LicenseInfo currentLicense;
    private List<Violation> violations = new ArrayList<>();
    private RenewalType renewalType = RenewalType.STANDARD;

    public static LicenseRenewalRequestBuilder builder() {
        return new LicenseRenewalRequestBuilder();
    }

    public LicenseRenewalRequestBuilder applicantId(String applicantId) {
        this.applicantId = applicantId;
        return this;
    }

    public LicenseRenewalRequestBuilder personalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
        return this;
    }

    public LicenseRenewalRequestBuilder currentLicense(LicenseInfo currentLicense) {
        this.currentLicense = currentLicense;
        return this;
    }

    public LicenseRenewalRequestBuilder violations(List<Violation> violations) {
        this.violations = violations;
        return this;
    }

    public LicenseRenewalRequestBuilder addViolation(Violation violation) {
        this.violations.add(violation);
        return this;
    }

    public LicenseRenewalRequestBuilder renewalType(RenewalType renewalType) {
        this.renewalType = renewalType;
        return this;
    }

    public LicenseRenewalRequest build() {
        LicenseRenewalRequest request = new LicenseRenewalRequest();
        request.setApplicantId(applicantId);
        request.setPersonalInfo(personalInfo);
        request.setCurrentLicense(currentLicense);
        request.setViolations(violations);
        request.setRenewalType(renewalType);
        return request;
    }

    // Predefined sample scenarios
    public static LicenseRenewalRequest createValidAdultRenewal() {
        PersonalInfo personalInfo = new PersonalInfo(
            "John", "Doe", LocalDate.of(1985, 5, 15),
            new Address("123 Main St", "Anytown", "CA", "12345"),
            "+1-555-123-4567"
        );

        LicenseInfo licenseInfo = new LicenseInfo(
            "D1234567", LicenseClass.CLASS_C,
            LocalDate.of(2020, 1, 1), LocalDate.of(2025, 1, 1),
            LicenseStatus.ACTIVE
        );

        return builder()
            .applicantId("APP001")
            .personalInfo(personalInfo)
            .currentLicense(licenseInfo)
            .renewalType(RenewalType.STANDARD)
            .build();
    }

    public static LicenseRenewalRequest createMinorRenewal() {
        PersonalInfo personalInfo = new PersonalInfo(
            "Jane", "Smith", LocalDate.of(2008, 3, 20),
            new Address("456 Oak Ave", "Somewhere", "CA", "54321"),
            "+1-555-987-6543"
        );

        LicenseInfo licenseInfo = new LicenseInfo(
            "L9876543", LicenseClass.LEARNER_PERMIT,
            LocalDate.of(2023, 6, 1), LocalDate.of(2024, 6, 1),
            LicenseStatus.ACTIVE
        );

        return builder()
            .applicantId("APP002")
            .personalInfo(personalInfo)
            .currentLicense(licenseInfo)
            .renewalType(RenewalType.STANDARD)
            .build();
    }

    public static LicenseRenewalRequest createExpiredLicenseRenewal() {
        PersonalInfo personalInfo = new PersonalInfo(
            "Bob", "Johnson", LocalDate.of(1975, 8, 10),
            new Address("789 Pine St", "Elsewhere", "CA", "98765"),
            "+1-555-456-7890"
        );

        LicenseInfo licenseInfo = new LicenseInfo(
            "D5555555", LicenseClass.CLASS_C,
            LocalDate.of(2018, 1, 1), LocalDate.of(2023, 1, 1),
            LicenseStatus.EXPIRED
        );

        return builder()
            .applicantId("APP003")
            .personalInfo(personalInfo)
            .currentLicense(licenseInfo)
            .renewalType(RenewalType.STANDARD)
            .build();
    }

    public static LicenseRenewalRequest createRenewalWithViolations() {
        PersonalInfo personalInfo = new PersonalInfo(
            "Alice", "Brown", LocalDate.of(1990, 12, 5),
            new Address("321 Elm St", "Nowhere", "CA", "13579"),
            "+1-555-321-6547"
        );

        LicenseInfo licenseInfo = new LicenseInfo(
            "D7777777", LicenseClass.CLASS_C,
            LocalDate.of(2021, 1, 1), LocalDate.of(2026, 1, 1),
            LicenseStatus.ACTIVE
        );

        Violation violation1 = new Violation(
            "SP001", "Speeding", LocalDate.of(2023, 6, 15),
            new BigDecimal("150.00"), ViolationStatus.OUTSTANDING
        );

        Violation violation2 = new Violation(
            "PK002", "Illegal Parking", LocalDate.of(2023, 8, 20),
            new BigDecimal("75.00"), ViolationStatus.RESOLVED
        );

        return builder()
            .applicantId("APP004")
            .personalInfo(personalInfo)
            .currentLicense(licenseInfo)
            .addViolation(violation1)
            .addViolation(violation2)
            .renewalType(RenewalType.STANDARD)
            .build();
    }
}
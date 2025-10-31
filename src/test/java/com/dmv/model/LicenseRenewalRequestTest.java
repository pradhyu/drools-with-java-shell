package com.dmv.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class LicenseRenewalRequestTest {

    @Test
    void testLicenseRenewalRequestCreation() {
        // Create test data
        Address address = new Address("123 Test St", "Test City", "CA", "12345");
        PersonalInfo personalInfo = new PersonalInfo("John", "Doe", LocalDate.of(1985, 1, 1), address, "+1-555-123-4567");
        LicenseInfo licenseInfo = new LicenseInfo("D1234567", LicenseClass.CLASS_C, 
                                                 LocalDate.of(2020, 1, 1), LocalDate.of(2026, 12, 31), LicenseStatus.ACTIVE);
        
        LicenseRenewalRequest request = new LicenseRenewalRequest("APP001", personalInfo, licenseInfo, RenewalType.STANDARD);
        
        // Assertions
        assertEquals("APP001", request.getApplicantId());
        assertEquals("John", request.getPersonalInfo().getFirstName());
        assertEquals("Doe", request.getPersonalInfo().getLastName());
        assertEquals(LicenseClass.CLASS_C, request.getCurrentLicense().getLicenseClass());
        assertEquals(RenewalType.STANDARD, request.getRenewalType());
        assertFalse(request.hasOutstandingViolations());
        assertEquals(0, request.getOutstandingViolationCount());
    }

    @Test
    void testPersonalInfoAgeCalculation() {
        Address address = new Address("123 Test St", "Test City", "CA", "12345");
        PersonalInfo personalInfo = new PersonalInfo("Jane", "Smith", LocalDate.of(2008, 1, 1), address, "+1-555-987-6543");
        
        int currentYear = LocalDate.now().getYear();
        int expectedAge = currentYear - 2008;
        
        assertEquals(expectedAge, personalInfo.getAge());
    }

    @Test
    void testLicenseInfoExpiration() {
        // Test expired license
        LicenseInfo expiredLicense = new LicenseInfo("D9999999", LicenseClass.CLASS_C,
                                                    LocalDate.of(2018, 1, 1), LocalDate.of(2023, 1, 1), LicenseStatus.EXPIRED);
        
        assertTrue(expiredLicense.isExpired());
        assertTrue(expiredLicense.getMonthsSinceExpiration() > 0);
        assertTrue(expiredLicense.getDaysUntilExpiration() < 0);
        
        // Test active license
        LicenseInfo activeLicense = new LicenseInfo("D1111111", LicenseClass.CLASS_C,
                                                   LocalDate.of(2020, 1, 1), LocalDate.of(2026, 12, 31), LicenseStatus.ACTIVE);
        
        assertFalse(activeLicense.isExpired());
        assertEquals(0, activeLicense.getMonthsSinceExpiration());
        assertTrue(activeLicense.getDaysUntilExpiration() > 0);
    }

    @Test
    void testViolationHandling() {
        Violation outstandingViolation = new Violation("SP001", "Speeding", LocalDate.of(2023, 6, 15),
                                                      new BigDecimal("150.00"), ViolationStatus.OUTSTANDING);
        
        Violation resolvedViolation = new Violation("PK002", "Illegal Parking", LocalDate.of(2023, 8, 20),
                                                   new BigDecimal("75.00"), ViolationStatus.RESOLVED);
        resolvedViolation.setResolvedDate(LocalDate.of(2023, 9, 1));
        
        assertTrue(outstandingViolation.isOutstanding());
        assertFalse(outstandingViolation.isResolved());
        
        assertFalse(resolvedViolation.isOutstanding());
        assertTrue(resolvedViolation.isResolved());
        assertNotNull(resolvedViolation.getResolvedDate());
        
        // Test request with violations
        Address address = new Address("123 Test St", "Test City", "CA", "12345");
        PersonalInfo personalInfo = new PersonalInfo("Alice", "Brown", LocalDate.of(1990, 1, 1), address, "+1-555-321-6547");
        LicenseInfo licenseInfo = new LicenseInfo("D7777777", LicenseClass.CLASS_C,
                                                 LocalDate.of(2021, 1, 1), LocalDate.of(2026, 1, 1), LicenseStatus.ACTIVE);
        
        LicenseRenewalRequest request = new LicenseRenewalRequest("APP004", personalInfo, licenseInfo, RenewalType.STANDARD);
        request.setViolations(Arrays.asList(outstandingViolation, resolvedViolation));
        
        assertTrue(request.hasOutstandingViolations());
        assertEquals(1, request.getOutstandingViolationCount());
    }

    @Test
    void testRenewalDecision() {
        RenewalDecision decision = new RenewalDecision(DecisionType.APPROVED);
        decision.addRequirement("Test requirement");
        decision.addReason("Test reason");
        decision.setFee(new BigDecimal("35.00"));
        decision.setValidUntil(LocalDate.of(2029, 1, 1));
        
        assertEquals(DecisionType.APPROVED, decision.getDecision());
        assertEquals(1, decision.getRequirements().size());
        assertEquals(1, decision.getReasons().size());
        assertEquals("Test requirement", decision.getRequirements().get(0));
        assertEquals("Test reason", decision.getReasons().get(0));
        assertEquals(new BigDecimal("35.00"), decision.getFee());
        assertEquals(LocalDate.of(2029, 1, 1), decision.getValidUntil());
    }

    @Test
    void testAddressValidation() {
        Address address = new Address("123 Main St", "Anytown", "CA", "12345");
        
        assertEquals("123 Main St", address.getStreet());
        assertEquals("Anytown", address.getCity());
        assertEquals("CA", address.getState());
        assertEquals("12345", address.getZipCode());
    }

    @Test
    void testEnumValues() {
        // Test LicenseClass enum
        assertEquals("Class C - Regular Driver's License", LicenseClass.CLASS_C.getDescription());
        assertEquals("Motorcycle License", LicenseClass.MOTORCYCLE.getDescription());
        
        // Test LicenseStatus enum
        assertEquals("Active", LicenseStatus.ACTIVE.getDescription());
        assertEquals("Expired", LicenseStatus.EXPIRED.getDescription());
        
        // Test DecisionType enum
        assertEquals("Approved", DecisionType.APPROVED.getDescription());
        assertEquals("Rejected", DecisionType.REJECTED.getDescription());
        assertEquals("Requires Action", DecisionType.REQUIRES_ACTION.getDescription());
        
        // Test RenewalType enum
        assertEquals("Standard Renewal", RenewalType.STANDARD.getDescription());
        assertEquals("Expedited Renewal", RenewalType.EXPEDITED.getDescription());
        
        // Test ViolationStatus enum
        assertEquals("Outstanding", ViolationStatus.OUTSTANDING.getDescription());
        assertEquals("Resolved", ViolationStatus.RESOLVED.getDescription());
    }
}
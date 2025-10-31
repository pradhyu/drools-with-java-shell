package com.dmv.service;

import com.dmv.model.*;
import com.dmv.model.builder.LicenseRenewalRequestBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RulesManagementServiceTest {

    @Test
    void testRuleCompilationResultStructure() {
        RuleCompilationResult result = new RuleCompilationResult(true);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertFalse(result.hasErrors());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testRuleExecutionResultStructure() {
        RuleExecutionResult result = new RuleExecutionResult(true);
        result.setRulesFired(2);
        result.setFiredRuleNames(Arrays.asList("Rule1", "Rule2"));
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(2, result.getRulesFired());
        assertEquals(2, result.getFiredRuleNames().size());
        assertTrue(result.getFiredRuleNames().contains("Rule1"));
        assertTrue(result.getFiredRuleNames().contains("Rule2"));
    }

    @Test
    void testRuleMetadataStructure() {
        RuleMetadata metadata = new RuleMetadata("TestRule", "A test rule");
        
        assertNotNull(metadata);
        assertEquals("TestRule", metadata.getRuleName());
        assertEquals("A test rule", metadata.getDescription());
    }

    @Test
    void testSampleDataCreation() {
        LicenseRenewalRequest adultRequest = LicenseRenewalRequestBuilder.createValidAdultRenewal();
        LicenseRenewalRequest minorRequest = LicenseRenewalRequestBuilder.createMinorRenewal();
        LicenseRenewalRequest expiredRequest = LicenseRenewalRequestBuilder.createExpiredLicenseRenewal();
        LicenseRenewalRequest violationsRequest = LicenseRenewalRequestBuilder.createRenewalWithViolations();
        
        assertNotNull(adultRequest);
        assertNotNull(minorRequest);
        assertNotNull(expiredRequest);
        assertNotNull(violationsRequest);
        
        assertTrue(adultRequest.getPersonalInfo().getAge() >= 18);
        assertTrue(minorRequest.getPersonalInfo().getAge() < 18);
        assertTrue(expiredRequest.getCurrentLicense().isExpired());
        assertTrue(violationsRequest.hasOutstandingViolations());
    }
}
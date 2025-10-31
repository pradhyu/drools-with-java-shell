package com.dmv.integration;

import com.dmv.model.*;
import com.dmv.model.builder.LicenseRenewalRequestBuilder;
import com.dmv.service.ExternalDataService;
import com.dmv.service.RulesManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "dmv.external-data.directory=src/main/resources/data"
})
class ExternalDataIntegrationTest {

    @Autowired
    private ExternalDataService externalDataService;

    @Autowired
    private RulesManagementService rulesManagementService;

    @Test
    void testExternalDataServiceBasicFunctionality() {
        // Test loading states collection
        List<Map<String, Object>> states = externalDataService.findByCollection("states");
        assertNotNull(states);
        assertFalse(states.isEmpty());
        
        // Test finding specific state
        List<Map<String, Object>> california = externalDataService.findByCollectionAndKey("states", "code", "CA");
        assertEquals(1, california.size());
        assertEquals("California", california.get(0).get("name"));
        assertEquals(true, california.get(0).get("requiresVisionTest"));
        
        // Test finding by key existence
        List<Map<String, Object>> statesWithVisionTest = externalDataService.findByCollectionAndKeyExists("states", "requiresVisionTest");
        assertFalse(statesWithVisionTest.isEmpty());
    }

    @Test
    void testLicenseClassDataAccess() {
        // Test loading license classes
        List<Map<String, Object>> licenseClasses = externalDataService.findByCollection("license-classes");
        assertNotNull(licenseClasses);
        assertFalse(licenseClasses.isEmpty());
        
        // Test finding specific license class
        List<Map<String, Object>> classC = externalDataService.findByCollectionAndKey("license-classes", "class", "CLASS_C");
        assertEquals(1, classC.size());
        assertEquals("Class C - Regular Driver License", classC.get(0).get("name"));
        
        // Test nested property access
        Map<String, Object> feeInfo = (Map<String, Object>) classC.get(0).get("fee");
        assertNotNull(feeInfo);
        assertEquals(35.0, feeInfo.get("base"));
        assertEquals(25.0, feeInfo.get("senior"));
    }

    @Test
    void testFeeScheduleDataAccess() {
        // Test loading fee schedules
        List<Map<String, Object>> feeSchedules = externalDataService.findByCollection("fee-schedules");
        assertNotNull(feeSchedules);
        assertFalse(feeSchedules.isEmpty());
        
        // Test finding renewal fees
        List<Map<String, Object>> renewalFees = externalDataService.findByCollectionAndKey("fee-schedules", "type", "renewal");
        assertFalse(renewalFees.isEmpty());
        
        // Test finding late renewal fees
        List<Map<String, Object>> lateFees = externalDataService.findByCollectionAndKey("fee-schedules", "type", "late_renewal");
        assertFalse(lateFees.isEmpty());
    }

    @Test
    void testCacheStatistics() {
        // Perform some operations to generate cache activity
        externalDataService.findByCollection("states");
        externalDataService.findByCollectionAndKey("states", "code", "CA");
        externalDataService.findByCollection("license-classes");
        
        // Get cache statistics
        var stats = externalDataService.getCacheStatistics();
        assertNotNull(stats);
        assertNotNull(stats.getLayerStats());
        assertEquals(2, stats.getLayerStats().size()); // Memory and Network cache layers
    }

    @Test
    void testAvailableCollections() {
        var collections = externalDataService.getAvailableCollections();
        assertNotNull(collections);
        assertTrue(collections.contains("states"));
        assertTrue(collections.contains("license-classes"));
        assertTrue(collections.contains("fee-schedules"));
    }

    @Test
    void testCacheInvalidation() {
        // Load some data to populate cache
        externalDataService.findByCollection("states");
        
        // Invalidate cache
        externalDataService.invalidateCache("states");
        
        // Should still work (will reload from JSON)
        List<Map<String, Object>> states = externalDataService.findByCollection("states");
        assertNotNull(states);
        assertFalse(states.isEmpty());
    }

    @Test
    void testRulesWithExternalData() {
        // Create a California resident renewal request
        LicenseRenewalRequest request = LicenseRenewalRequestBuilder.createValidAdultRenewal();
        
        // Set California address to trigger state-specific rules
        Address caAddress = new Address("123 Main St", "Los Angeles", "CA", "90210");
        PersonalInfo personalInfo = new PersonalInfo(
            request.getPersonalInfo().getFirstName(),
            request.getPersonalInfo().getLastName(),
            request.getPersonalInfo().getDateOfBirth(),
            caAddress,
            request.getPersonalInfo().getPhoneNumber()
        );
        
        LicenseRenewalRequest caRequest = new LicenseRenewalRequest(
            request.getApplicantId(),
            personalInfo,
            request.getCurrentLicense(),
            request.getRenewalType()
        );

        // Execute rules
        RuleExecutionResult result = rulesManagementService.executeRules(Arrays.asList(caRequest));
        
        // Verify results
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertFalse(result.getModifiedFacts().isEmpty());
        
        // Check if a decision was created
        boolean decisionFound = result.getModifiedFacts().stream()
            .anyMatch(fact -> fact instanceof RenewalDecision);
        assertTrue(decisionFound);
        
        // Find the decision and check if external data was used
        RenewalDecision decision = (RenewalDecision) result.getModifiedFacts().stream()
            .filter(fact -> fact instanceof RenewalDecision)
            .findFirst()
            .orElse(null);
        
        assertNotNull(decision);
        
        // Check if California-specific requirements were added
        boolean hasVisionTestRequirement = decision.getRequirements().stream()
            .anyMatch(req -> req.contains("Vision test") || req.contains("vision test"));
        
        // Check if fee was calculated
        assertNotNull(decision.getFee());
        assertTrue(decision.getFee().compareTo(BigDecimal.ZERO) > 0);
        
        // Check if reasons mention external data usage
        boolean hasExternalDataReason = decision.getReasons().stream()
            .anyMatch(reason -> reason.contains("California") || reason.contains("CA") || 
                               reason.contains("Fee calculated") || reason.contains("State"));
        
        assertTrue(hasExternalDataReason, "Decision should include reasons based on external data");
    }

    @Test
    void testWarmUpCache() {
        // Warm up cache for states collection
        externalDataService.warmUpCache("states");
        
        // Subsequent access should be faster (from cache)
        List<Map<String, Object>> states = externalDataService.findByCollection("states");
        assertNotNull(states);
        assertFalse(states.isEmpty());
    }
}
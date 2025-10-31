package com.dmv.controller;

import com.dmv.model.*;
import com.dmv.model.builder.LicenseRenewalRequestBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DmvEvaluationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    void testControllerExists() {
        DmvEvaluationController controller = new DmvEvaluationController(null);
        assertNotNull(controller);
    }

    @Test
    void testObjectMapperSerialization() throws Exception {
        LicenseRenewalRequest request = LicenseRenewalRequestBuilder.createValidAdultRenewal();
        
        String json = objectMapper.writeValueAsString(request);
        assertNotNull(json);
        assertFalse(json.isEmpty());
        
        LicenseRenewalRequest deserialized = objectMapper.readValue(json, LicenseRenewalRequest.class);
        assertNotNull(deserialized);
        assertEquals(request.getApplicantId(), deserialized.getApplicantId());
    }

    @Test
    void testSampleDataCreation() {
        LicenseRenewalRequest request = LicenseRenewalRequestBuilder.createValidAdultRenewal();
        
        assertNotNull(request);
        assertNotNull(request.getApplicantId());
        assertNotNull(request.getPersonalInfo());
        assertNotNull(request.getCurrentLicense());
        assertTrue(request.getPersonalInfo().getAge() >= 18);
    }

    @Test
    void testEvaluationResponseStructure() {
        EvaluationResponse response = new EvaluationResponse();
        response.setApplicantId("APP001");
        
        RenewalDecision decision = new RenewalDecision(DecisionType.APPROVED);
        decision.addReason("Test approval");
        response.setDecision(decision);
        
        ExecutionMetadata metadata = new ExecutionMetadata();
        metadata.setRulesFired(2);
        response.setExecutionMetadata(metadata);
        
        assertNotNull(response);
        assertEquals("APP001", response.getApplicantId());
        assertNotNull(response.getDecision());
        assertEquals(DecisionType.APPROVED, response.getDecision().getDecision());
        assertNotNull(response.getExecutionMetadata());
        assertEquals(2, response.getExecutionMetadata().getRulesFired());
    }
}
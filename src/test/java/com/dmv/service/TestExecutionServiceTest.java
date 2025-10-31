package com.dmv.service;

import com.dmv.model.*;
import com.dmv.model.builder.LicenseRenewalRequestBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestExecutionServiceTest {

    @Test
    void testTestScenarioCreation() {
        TestScenario scenario = new TestScenario("test-scenario", "Test scenario description");
        scenario.addInputFact(LicenseRenewalRequestBuilder.createValidAdultRenewal());
        
        ExpectedOutcome outcome = new ExpectedOutcome(
            ExpectedOutcome.OutcomeType.RULE_FIRED,
            "Rule should fire",
            "TestRule"
        );
        scenario.addExpectedOutcome(outcome);
        
        assertNotNull(scenario);
        assertEquals("test-scenario", scenario.getScenarioName());
        assertEquals("Test scenario description", scenario.getDescription());
        assertFalse(scenario.getInputFacts().isEmpty());
        assertFalse(scenario.getExpectedOutcomes().isEmpty());
    }

    @Test
    void testTestResult() {
        TestResult result = new TestResult("test-scenario");
        result.setSuccess(true);
        result.setExecutionDurationMs(150L);
        result.addRuleFired("TestRule");
        
        assertNotNull(result);
        assertEquals("test-scenario", result.getScenarioName());
        assertTrue(result.isSuccess());
        assertEquals(150L, result.getExecutionDurationMs());
        assertTrue(result.getRulesFired().contains("TestRule"));
    }

    @Test
    void testBatchTestResult() {
        TestResult result1 = new TestResult("test1");
        result1.setSuccess(true);
        
        TestResult result2 = new TestResult("test2");
        result2.setSuccess(false);
        
        BatchTestResult batchResult = new BatchTestResult();
        batchResult.addTestResult(result1);
        batchResult.addTestResult(result2);
        
        BatchTestResult.BatchStatistics stats = new BatchTestResult.BatchStatistics(2, 1, 1, 0, 0, 0, 0.0);
        batchResult.setStatistics(stats);
        
        assertNotNull(batchResult);
        assertEquals(2, batchResult.getTestResults().size());
        assertNotNull(batchResult.getStatistics());
        assertEquals(2, batchResult.getStatistics().getTotalTests());
        assertEquals(1, batchResult.getStatistics().getPassedTests());
        assertEquals(1, batchResult.getStatistics().getFailedTests());
    }

    @Test
    void testExpectedOutcome() {
        ExpectedOutcome outcome = new ExpectedOutcome(
            ExpectedOutcome.OutcomeType.DECISION_MADE,
            "Should approve",
            "APPROVED"
        );
        
        assertNotNull(outcome);
        assertEquals(ExpectedOutcome.OutcomeType.DECISION_MADE, outcome.getType());
        assertEquals("Should approve", outcome.getDescription());
        assertEquals("APPROVED", outcome.getExpectedValue());
    }

    @Test
    void testTestValidationResult() {
        TestValidationResult result = new TestValidationResult();
        result.setValid(true);
        
        assertNotNull(result);
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testTestReport() {
        TestResult result1 = new TestResult("test1");
        result1.setSuccess(true);
        
        TestResult result2 = new TestResult("test2");
        result2.setSuccess(false);
        
        TestReport report = new TestReport();
        report.addTestResult(result1);
        report.addTestResult(result2);
        
        TestReport.TestSummary summary = new TestReport.TestSummary(2, 1, 1, 0.0);
        report.setSummary(summary);
        
        assertNotNull(report);
        assertEquals(2, report.getTestResults().size());
        assertNotNull(report.getSummary());
        assertEquals(2, report.getSummary().getTotalTests());
    }

    @Test
    void testSimulationResult() {
        SimulationResult result = new SimulationResult();
        result.setTotalExecutionTimeMs(200L);
        result.setTotalExecutions(10);
        result.setSuccessfulExecutions(8);
        result.setFailedExecutions(2);
        
        assertNotNull(result);
        assertEquals(200L, result.getTotalExecutionTimeMs());
        assertEquals(10, result.getTotalExecutions());
        assertEquals(8, result.getSuccessfulExecutions());
        assertEquals(2, result.getFailedExecutions());
        assertEquals(80.0, result.getSuccessRate(), 0.01);
    }

    @Test
    void testOutcomeResult() {
        OutcomeResult result = new OutcomeResult();
        result.setExpected("APPROVED");
        result.setActual("APPROVED");
        result.setMatched(true);
        
        assertNotNull(result);
        assertEquals("APPROVED", result.getExpected());
        assertEquals("APPROVED", result.getActual());
        assertTrue(result.isMatched());
    }

    @Test
    void testLoadTestConfig() {
        LoadTestConfig config = new LoadTestConfig();
        config.setConcurrentUsers(10);
        config.setDurationSeconds(60);
        config.setRampUpSeconds(10);
        
        assertNotNull(config);
        assertEquals(10, config.getConcurrentUsers());
        assertEquals(60, config.getDurationSeconds());
        assertEquals(10, config.getRampUpSeconds());
    }
}
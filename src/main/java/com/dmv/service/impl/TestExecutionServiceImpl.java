package com.dmv.service.impl;

import com.dmv.model.*;
import com.dmv.service.JsonFactsConverterService;
import com.dmv.service.RulesManagementService;
import com.dmv.service.TestExecutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TestExecutionServiceImpl implements TestExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(TestExecutionServiceImpl.class);

    private final RulesManagementService rulesManagementService;
    private final JsonFactsConverterService jsonFactsConverterService;
    private final ObjectMapper objectMapper;
    private final Map<String, TestScenario> savedScenarios = new ConcurrentHashMap<>();

    @Autowired
    public TestExecutionServiceImpl(RulesManagementService rulesManagementService, 
                                  JsonFactsConverterService jsonFactsConverterService) {
        this.rulesManagementService = rulesManagementService;
        this.jsonFactsConverterService = jsonFactsConverterService;
        this.objectMapper = jsonFactsConverterService.getObjectMapper();
        
        // Initialize with some default test scenarios
        initializeDefaultScenarios();
        
        logger.info("Test execution service initialized");
    }

    @Override
    public TestResult executeTestScenario(TestScenario scenario) {
        logger.info("Executing test scenario: {}", scenario.getScenarioName());
        
        TestResult result = new TestResult(scenario.getScenarioName());
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate scenario first
            TestValidationResult validation = validateTestScenario(scenario);
            if (!validation.isValid()) {
                result.setSuccess(false);
                result.setErrorMessage("Scenario validation failed: " + validation.getErrors());
                return result;
            }
            
            // Execute rules against input facts
            RuleExecutionResult executionResult = rulesManagementService.executeRules(scenario.getInputFacts());
            
            if (!executionResult.isSuccess()) {
                result.setSuccess(false);
                result.setErrorMessage("Rule execution failed: " + executionResult.getErrorMessage());
                return result;
            }
            
            // Set execution details
            result.setRulesFired(executionResult.getFiredRuleNames());
            result.setResultingFacts(executionResult.getModifiedFacts());
            
            // Evaluate expected outcomes
            boolean allOutcomesPassed = true;
            for (ExpectedOutcome expectedOutcome : scenario.getExpectedOutcomes()) {
                TestResult.OutcomeResult outcomeResult = evaluateOutcome(expectedOutcome, executionResult);
                result.addOutcomeResult(outcomeResult);
                
                if (!outcomeResult.isPassed()) {
                    allOutcomesPassed = false;
                }
            }
            
            result.setSuccess(allOutcomesPassed);
            
            // Add metrics
            result.addMetric("rulesExecuted", executionResult.getRulesFired());
            result.addMetric("factsProcessed", scenario.getInputFacts().size());
            result.addMetric("outcomesEvaluated", scenario.getExpectedOutcomes().size());
            
            logger.info("Test scenario '{}' completed with result: {}", scenario.getScenarioName(), 
                       allOutcomesPassed ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("Test execution error: " + e.getMessage());
            logger.error("Error executing test scenario: {}", scenario.getScenarioName(), e);
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            result.setExecutionDurationMs(executionTime);
        }
        
        return result;
    }

    @Override
    public BatchTestResult executeBatchTests(List<TestScenario> scenarios) {
        logger.info("Executing batch test with {} scenarios", scenarios.size());
        
        BatchTestResult batchResult = new BatchTestResult("Batch-" + LocalDateTime.now());
        long startTime = System.currentTimeMillis();
        
        for (TestScenario scenario : scenarios) {
            TestResult result = executeTestScenario(scenario);
            batchResult.addTestResult(result);
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        batchResult.setTotalExecutionTimeMs(totalTime);
        
        // Add aggregated metrics
        batchResult.addAggregatedMetric("totalScenariosExecuted", scenarios.size());
        batchResult.addAggregatedMetric("totalExecutionTimeMs", totalTime);
        batchResult.addAggregatedMetric("averageTimePerScenario", 
                                       scenarios.size() > 0 ? totalTime / scenarios.size() : 0);
        
        logger.info("Batch test completed. Results: {}", batchResult.getStatistics());
        
        return batchResult;
    }

    @Override
    public SimulationResult simulateLoad(LoadTestConfig config) {
        logger.info("Starting load simulation with config: {}", config);
        
        // This is a simplified implementation - could be enhanced with actual concurrent execution
        SimulationResult result = new SimulationResult();
        result.setConfig(config);
        result.setStartTime(LocalDateTime.now());
        
        long startTime = System.currentTimeMillis();
        
        try {
            List<TestResult> allResults = new ArrayList<>();
            
            // Simulate concurrent executions
            for (int i = 0; i < config.getConcurrentUsers(); i++) {
                for (int j = 0; j < config.getExecutionsPerUser(); j++) {
                    TestScenario scenario = config.getTestScenarioObject();
                    TestResult testResult = executeTestScenario(scenario);
                    allResults.add(testResult);
                }
            }
            
            // Calculate performance metrics
            long totalTime = System.currentTimeMillis() - startTime;
            double averageResponseTime = allResults.stream()
                .mapToLong(TestResult::getExecutionDurationMs)
                .average()
                .orElse(0.0);
            
            long successfulExecutions = allResults.stream()
                .mapToLong(r -> r.isSuccess() ? 1 : 0)
                .sum();
            
            result.setEndTime(LocalDateTime.now());
            result.setTotalExecutionTimeMs(totalTime);
            result.setTotalExecutions(allResults.size());
            result.setSuccessfulExecutions((int) successfulExecutions);
            result.setFailedExecutions(allResults.size() - (int) successfulExecutions);
            result.setAverageResponseTimeMs(averageResponseTime);
            result.setThroughputPerSecond(allResults.size() / (totalTime / 1000.0));
            
            logger.info("Load simulation completed: {} executions in {}ms", 
                       allResults.size(), totalTime);
            
        } catch (Exception e) {
            result.setErrorMessage("Load simulation failed: " + e.getMessage());
            logger.error("Error during load simulation", e);
        }
        
        return result;
    }

    @Override
    public TestReport generateTestReport(List<TestResult> results) {
        logger.info("Generating test report for {} results", results.size());
        
        TestReport report = new TestReport();
        report.setGeneratedAt(LocalDateTime.now());
        report.setTestResults(results);
        
        // Calculate summary statistics
        int totalTests = results.size();
        int passedTests = (int) results.stream().filter(TestResult::isSuccess).count();
        int failedTests = totalTests - passedTests;
        
        double averageExecutionTime = results.stream()
            .mapToLong(TestResult::getExecutionDurationMs)
            .average()
            .orElse(0.0);
        
        report.setSummary(new TestReport.TestSummary(
            totalTests, passedTests, failedTests, averageExecutionTime
        ));
        
        logger.info("Test report generated: {} total tests, {} passed, {} failed", 
                   totalTests, passedTests, failedTests);
        
        return report;
    }

    @Override
    public TestScenario createTestScenarioFromJson(String scenarioJson) {
        logger.debug("Creating test scenario from JSON");
        
        try {
            TestScenario scenario = objectMapper.readValue(scenarioJson, TestScenario.class);
            logger.debug("Successfully created test scenario: {}", scenario.getScenarioName());
            return scenario;
            
        } catch (Exception e) {
            logger.error("Error creating test scenario from JSON", e);
            throw new RuntimeException("Failed to create test scenario from JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public TestValidationResult validateTestScenario(TestScenario scenario) {
        TestValidationResult result = new TestValidationResult(true);
        
        if (scenario.getScenarioName() == null || scenario.getScenarioName().trim().isEmpty()) {
            result.addError("Scenario name is required");
        }
        
        if (scenario.getInputFacts() == null || scenario.getInputFacts().isEmpty()) {
            result.addError("Input facts are required");
        }
        
        if (scenario.getExpectedOutcomes() == null || scenario.getExpectedOutcomes().isEmpty()) {
            result.addWarning("No expected outcomes defined - test will only verify execution");
        }
        
        return result;
    }

    @Override
    public List<String> getAvailableTestScenarios() {
        return new ArrayList<>(savedScenarios.keySet());
    }

    @Override
    public void saveTestScenario(TestScenario scenario, String name) {
        logger.info("Saving test scenario: {}", name);
        savedScenarios.put(name, scenario);
    }

    @Override
    public TestScenario loadTestScenario(String name) {
        logger.debug("Loading test scenario: {}", name);
        return savedScenarios.get(name);
    }

    private TestResult.OutcomeResult evaluateOutcome(ExpectedOutcome expectedOutcome, RuleExecutionResult executionResult) {
        TestResult.OutcomeResult result = new TestResult.OutcomeResult();
        result.setExpectedOutcome(expectedOutcome);
        
        try {
            switch (expectedOutcome.getType()) {
                case RULE_FIRED:
                    boolean ruleFired = executionResult.getFiredRuleNames().contains(expectedOutcome.getExpectedValue());
                    result.setPassed(ruleFired);
                    result.setActualValue(ruleFired);
                    result.setMessage(ruleFired ? "Rule fired as expected" : "Rule did not fire");
                    break;
                    
                case RULE_NOT_FIRED:
                    boolean ruleNotFired = !executionResult.getFiredRuleNames().contains(expectedOutcome.getExpectedValue());
                    result.setPassed(ruleNotFired);
                    result.setActualValue(!ruleNotFired);
                    result.setMessage(ruleNotFired ? "Rule correctly did not fire" : "Rule unexpectedly fired");
                    break;
                    
                case PROPERTY_VALUE:
                    Object actualValue = getPropertyValue(executionResult.getModifiedFacts(), 
                                                        expectedOutcome.getFactType(), 
                                                        expectedOutcome.getPropertyPath());
                    boolean valueMatches = expectedOutcome.getExpectedValue().equals(actualValue);
                    result.setPassed(valueMatches);
                    result.setActualValue(actualValue);
                    result.setMessage(valueMatches ? "Property value matches" : 
                                     "Expected: " + expectedOutcome.getExpectedValue() + ", Actual: " + actualValue);
                    break;
                    
                default:
                    result.setPassed(false);
                    result.setMessage("Unsupported outcome type: " + expectedOutcome.getType());
            }
            
        } catch (Exception e) {
            result.setPassed(false);
            result.setMessage("Error evaluating outcome: " + e.getMessage());
            logger.error("Error evaluating outcome", e);
        }
        
        return result;
    }

    private Object getPropertyValue(List<Object> facts, String factType, String propertyPath) {
        for (Object fact : facts) {
            if (fact.getClass().getSimpleName().equals(factType)) {
                return getNestedProperty(fact, propertyPath);
            }
        }
        return null;
    }

    private Object getNestedProperty(Object object, String propertyPath) {
        try {
            String[] parts = propertyPath.split("\\.");
            Object current = object;
            
            for (String part : parts) {
                String methodName = "get" + part.substring(0, 1).toUpperCase() + part.substring(1);
                Method method = current.getClass().getMethod(methodName);
                current = method.invoke(current);
                
                if (current == null) {
                    break;
                }
            }
            
            return current;
            
        } catch (Exception e) {
            logger.error("Error getting nested property: {}", propertyPath, e);
            return null;
        }
    }

    private void initializeDefaultScenarios() {
        // Create some default test scenarios for DMV rules
        
        // Scenario 1: Valid adult renewal
        TestScenario adultRenewal = new TestScenario("valid-adult-renewal", "Valid adult license renewal");
        adultRenewal.addInputFact(com.dmv.model.builder.LicenseRenewalRequestBuilder.createValidAdultRenewal());
        adultRenewal.addExpectedOutcome(new ExpectedOutcome(
            ExpectedOutcome.OutcomeType.RULE_FIRED, 
            "Age verification rule should fire", 
            "Age Verification - Adult Approved"
        ));
        adultRenewal.addExpectedOutcome(new ExpectedOutcome(
            ExpectedOutcome.OutcomeType.PROPERTY_VALUE,
            "Decision should be approved",
            DecisionType.APPROVED
        ));
        adultRenewal.setCategory("positive");
        adultRenewal.addTag("adult");
        adultRenewal.addTag("valid");
        
        savedScenarios.put("valid-adult-renewal", adultRenewal);
        
        // Scenario 2: Minor renewal requiring parental consent
        TestScenario minorRenewal = new TestScenario("minor-renewal", "Minor license renewal requiring parental consent");
        minorRenewal.addInputFact(com.dmv.model.builder.LicenseRenewalRequestBuilder.createMinorRenewal());
        minorRenewal.addExpectedOutcome(new ExpectedOutcome(
            ExpectedOutcome.OutcomeType.RULE_FIRED,
            "Age verification rule should fire",
            "Age Verification - Minor Requires Parental Consent"
        ));
        minorRenewal.addExpectedOutcome(new ExpectedOutcome(
            ExpectedOutcome.OutcomeType.PROPERTY_VALUE,
            "Decision should require action",
            DecisionType.REQUIRES_ACTION
        ));
        minorRenewal.setCategory("conditional");
        minorRenewal.addTag("minor");
        minorRenewal.addTag("parental-consent");
        
        savedScenarios.put("minor-renewal", minorRenewal);
        
        logger.info("Initialized {} default test scenarios", savedScenarios.size());
    }
}
package com.dmv.service;

import com.dmv.model.*;

import java.util.List;

/**
 * Service interface for rule testing and simulation
 */
public interface TestExecutionService {
    
    /**
     * Execute a single test scenario
     * @param scenario Test scenario to execute
     * @return Test result with execution details
     */
    TestResult executeTestScenario(TestScenario scenario);
    
    /**
     * Execute multiple test scenarios in batch
     * @param scenarios List of test scenarios
     * @return Batch test result with aggregated information
     */
    BatchTestResult executeBatchTests(List<TestScenario> scenarios);
    
    /**
     * Simulate load testing with multiple concurrent executions
     * @param config Load test configuration
     * @return Simulation result with performance metrics
     */
    SimulationResult simulateLoad(LoadTestConfig config);
    
    /**
     * Generate a comprehensive test report
     * @param results List of test results
     * @return Generated test report
     */
    TestReport generateTestReport(List<TestResult> results);
    
    /**
     * Create a test scenario from JSON input
     * @param scenarioJson JSON representation of test scenario
     * @return Created test scenario
     */
    TestScenario createTestScenarioFromJson(String scenarioJson);
    
    /**
     * Validate a test scenario
     * @param scenario Test scenario to validate
     * @return Validation result
     */
    TestValidationResult validateTestScenario(TestScenario scenario);
    
    /**
     * Get all available test scenarios
     * @return List of test scenario names
     */
    List<String> getAvailableTestScenarios();
    
    /**
     * Save a test scenario for reuse
     * @param scenario Test scenario to save
     * @param name Name for the scenario
     */
    void saveTestScenario(TestScenario scenario, String name);
    
    /**
     * Load a saved test scenario
     * @param name Name of the scenario
     * @return Loaded test scenario or null if not found
     */
    TestScenario loadTestScenario(String name);
}
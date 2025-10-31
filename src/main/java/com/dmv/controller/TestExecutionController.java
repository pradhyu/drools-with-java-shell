package com.dmv.controller;

import com.dmv.model.*;
import com.dmv.service.TestExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for test execution and simulation
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestExecutionController {

    private static final Logger logger = LoggerFactory.getLogger(TestExecutionController.class);

    private final TestExecutionService testExecutionService;

    @Autowired
    public TestExecutionController(TestExecutionService testExecutionService) {
        this.testExecutionService = testExecutionService;
    }

    /**
     * Execute a single test scenario
     */
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeTestScenario(@RequestBody TestScenario scenario) {
        logger.info("Executing test scenario: {}", scenario.getScenarioName());
        
        try {
            TestResult result = testExecutionService.executeTestScenario(scenario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("testResult", result);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error executing test scenario: {}", scenario.getScenarioName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("EXECUTION_ERROR", "Failed to execute test scenario: " + e.getMessage()));
        }
    }

    /**
     * Execute multiple test scenarios in batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> executeBatchTests(@RequestBody List<TestScenario> scenarios) {
        logger.info("Executing batch test with {} scenarios", scenarios.size());
        
        try {
            BatchTestResult result = testExecutionService.executeBatchTests(scenarios);
            
            Map<String, Object> response = new HashMap<>();
            response.put("batchResult", result);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error executing batch tests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("BATCH_EXECUTION_ERROR", "Failed to execute batch tests: " + e.getMessage()));
        }
    }

    /**
     * Run load simulation
     */
    @PostMapping("/simulate")
    public ResponseEntity<Map<String, Object>> simulateLoad(@RequestBody LoadTestConfig config) {
        logger.info("Starting load simulation: {}", config);
        
        try {
            SimulationResult result = testExecutionService.simulateLoad(config);
            
            Map<String, Object> response = new HashMap<>();
            response.put("simulationResult", result);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error running load simulation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("SIMULATION_ERROR", "Failed to run load simulation: " + e.getMessage()));
        }
    }

    /**
     * Generate test report
     */
    @PostMapping("/report")
    public ResponseEntity<Map<String, Object>> generateTestReport(@RequestBody List<TestResult> results) {
        logger.info("Generating test report for {} results", results.size());
        
        try {
            TestReport report = testExecutionService.generateTestReport(results);
            
            Map<String, Object> response = new HashMap<>();
            response.put("report", report);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error generating test report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("REPORT_ERROR", "Failed to generate test report: " + e.getMessage()));
        }
    }

    /**
     * Validate a test scenario
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateTestScenario(@RequestBody TestScenario scenario) {
        logger.debug("Validating test scenario: {}", scenario.getScenarioName());
        
        try {
            TestValidationResult result = testExecutionService.validateTestScenario(scenario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("validationResult", result);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error validating test scenario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("VALIDATION_ERROR", "Failed to validate test scenario: " + e.getMessage()));
        }
    }

    /**
     * Create test scenario from JSON
     */
    @PostMapping("/scenario/create")
    public ResponseEntity<Map<String, Object>> createTestScenarioFromJson(@RequestBody Map<String, String> request) {
        String scenarioJson = request.get("scenarioJson");
        
        if (scenarioJson == null || scenarioJson.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_REQUEST", "Scenario JSON is required"));
        }
        
        logger.debug("Creating test scenario from JSON");
        
        try {
            TestScenario scenario = testExecutionService.createTestScenarioFromJson(scenarioJson);
            
            Map<String, Object> response = new HashMap<>();
            response.put("scenario", scenario);
            response.put("message", "Test scenario created successfully");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error creating test scenario from JSON", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("JSON_PARSE_ERROR", "Failed to create test scenario from JSON: " + e.getMessage()));
        }
    }

    /**
     * Get available test scenarios
     */
    @GetMapping("/scenarios")
    public ResponseEntity<Map<String, Object>> getAvailableTestScenarios() {
        logger.debug("Getting available test scenarios");
        
        try {
            List<String> scenarios = testExecutionService.getAvailableTestScenarios();
            
            Map<String, Object> response = new HashMap<>();
            response.put("scenarios", scenarios);
            response.put("count", scenarios.size());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting available test scenarios", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("SCENARIOS_ERROR", "Failed to get available test scenarios: " + e.getMessage()));
        }
    }

    /**
     * Save a test scenario
     */
    @PostMapping("/scenarios/{name}")
    public ResponseEntity<Map<String, Object>> saveTestScenario(@PathVariable String name, @RequestBody TestScenario scenario) {
        logger.info("Saving test scenario: {}", name);
        
        try {
            testExecutionService.saveTestScenario(scenario, name);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test scenario saved successfully");
            response.put("scenarioName", name);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error saving test scenario: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("SAVE_ERROR", "Failed to save test scenario: " + e.getMessage()));
        }
    }

    /**
     * Load a saved test scenario
     */
    @GetMapping("/scenarios/{name}")
    public ResponseEntity<Map<String, Object>> loadTestScenario(@PathVariable String name) {
        logger.debug("Loading test scenario: {}", name);
        
        try {
            TestScenario scenario = testExecutionService.loadTestScenario(name);
            
            if (scenario == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("scenario", scenario);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error loading test scenario: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("LOAD_ERROR", "Failed to load test scenario: " + e.getMessage()));
        }
    }

    /**
     * Execute a saved test scenario by name
     */
    @PostMapping("/scenarios/{name}/execute")
    public ResponseEntity<Map<String, Object>> executeSavedScenario(@PathVariable String name) {
        logger.info("Executing saved test scenario: {}", name);
        
        try {
            TestScenario scenario = testExecutionService.loadTestScenario(name);
            
            if (scenario == null) {
                return ResponseEntity.notFound().build();
            }
            
            TestResult result = testExecutionService.executeTestScenario(scenario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("testResult", result);
            response.put("scenarioName", name);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error executing saved test scenario: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("EXECUTION_ERROR", "Failed to execute saved test scenario: " + e.getMessage()));
        }
    }

    /**
     * Get performance metrics for recent test executions
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        logger.debug("Getting performance metrics");
        
        try {
            // This would typically aggregate metrics from a metrics store
            // For now, return basic system information
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("availableScenarios", testExecutionService.getAvailableTestScenarios().size());
            metrics.put("systemTime", LocalDateTime.now());
            metrics.put("jvmMemory", Map.of(
                "totalMemory", Runtime.getRuntime().totalMemory(),
                "freeMemory", Runtime.getRuntime().freeMemory(),
                "maxMemory", Runtime.getRuntime().maxMemory()
            ));
            
            Map<String, Object> response = new HashMap<>();
            response.put("metrics", metrics);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting performance metrics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("METRICS_ERROR", "Failed to get performance metrics: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String errorCode, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", Map.of(
            "code", errorCode,
            "message", message,
            "timestamp", LocalDateTime.now()
        ));
        return error;
    }
}
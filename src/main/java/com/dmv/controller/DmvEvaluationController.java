package com.dmv.controller;

import com.dmv.model.LicenseRenewalRequest;
import com.dmv.model.RenewalDecision;
import com.dmv.model.RuleExecutionResult;
import com.dmv.service.RulesManagementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for DMV license renewal evaluation
 */
@RestController
@RequestMapping("/api/dmv")
@CrossOrigin(origins = "*")
public class DmvEvaluationController {

    private static final Logger logger = LoggerFactory.getLogger(DmvEvaluationController.class);

    private final RulesManagementService rulesManagementService;

    @Autowired
    public DmvEvaluationController(RulesManagementService rulesManagementService) {
        this.rulesManagementService = rulesManagementService;
    }

    /**
     * Evaluate a license renewal request using Drools rules
     */
    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateRenewalRequest(@Valid @RequestBody LicenseRenewalRequest request) {
        logger.info("Evaluating renewal request for applicant: {}", request.getApplicantId());
        
        try {
            // Execute rules against the request
            RuleExecutionResult executionResult = rulesManagementService.executeRules(Arrays.asList(request));
            
            if (!executionResult.isSuccess()) {
                logger.error("Rule execution failed: {}", executionResult.getErrorMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("RULE_EXECUTION_ERROR", executionResult.getErrorMessage()));
            }

            // Extract the decision from modified facts
            RenewalDecision decision = null;
            for (Object fact : executionResult.getModifiedFacts()) {
                if (fact instanceof RenewalDecision) {
                    decision = (RenewalDecision) fact;
                    break;
                }
            }

            if (decision == null) {
                logger.warn("No decision generated for request: {}", request.getApplicantId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("NO_DECISION_GENERATED", "Rules did not generate a decision"));
            }

            // Create response with decision and execution metadata
            Map<String, Object> response = new HashMap<>();
            response.put("applicantId", request.getApplicantId());
            response.put("decision", decision);
            response.put("executionMetadata", Map.of(
                "rulesFired", executionResult.getRulesFired(),
                "firedRuleNames", executionResult.getFiredRuleNames(),
                "executionTimeMs", executionResult.getExecutionTimeMs()
            ));

            logger.info("Renewal evaluation completed for applicant: {} with decision: {}", 
                       request.getApplicantId(), decision.getDecision());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error evaluating renewal request for applicant: {}", request.getApplicantId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("EVALUATION_ERROR", "Failed to evaluate renewal request: " + e.getMessage()));
        }
    }

    /**
     * Get evaluation status and system health
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getEvaluationStatus() {
        logger.debug("Getting evaluation system status");
        
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("status", "ACTIVE");
            status.put("rulesLoaded", rulesManagementService.getLoadedRules().size());
            status.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            logger.error("Error getting system status", e);
            Map<String, Object> errorStatus = new HashMap<>();
            errorStatus.put("status", "ERROR");
            errorStatus.put("error", e.getMessage());
            errorStatus.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorStatus);
        }
    }

    /**
     * Validate a renewal request without executing rules
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateRenewalRequest(@Valid @RequestBody LicenseRenewalRequest request) {
        logger.debug("Validating renewal request for applicant: {}", request.getApplicantId());
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("applicantId", request.getApplicantId());
            response.put("message", "Request validation successful");
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error validating renewal request", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("VALIDATION_ERROR", "Request validation failed: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String errorCode, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", Map.of(
            "code", errorCode,
            "message", message,
            "timestamp", java.time.LocalDateTime.now()
        ));
        return error;
    }
}
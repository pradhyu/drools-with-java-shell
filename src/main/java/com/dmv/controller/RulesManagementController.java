package com.dmv.controller;

import com.dmv.model.RuleCompilationResult;
import com.dmv.model.RuleMetadata;
import com.dmv.service.RulesManagementService;
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
 * REST controller for rules management operations
 */
@RestController
@RequestMapping("/api/rules")
@CrossOrigin(origins = "*")
public class RulesManagementController {

    private static final Logger logger = LoggerFactory.getLogger(RulesManagementController.class);

    private final RulesManagementService rulesManagementService;

    @Autowired
    public RulesManagementController(RulesManagementService rulesManagementService) {
        this.rulesManagementService = rulesManagementService;
    }

    /**
     * Get list of all currently loaded rules
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getLoadedRules() {
        logger.debug("Getting list of loaded rules");
        
        try {
            List<RuleMetadata> rules = rulesManagementService.getLoadedRules();
            
            Map<String, Object> response = new HashMap<>();
            response.put("rules", rules);
            response.put("count", rules.size());
            response.put("timestamp", LocalDateTime.now());
            
            logger.debug("Retrieved {} loaded rules", rules.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving loaded rules", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("RULES_RETRIEVAL_ERROR", "Failed to retrieve rules: " + e.getMessage()));
        }
    }

    /**
     * Reload all rules from the rules directory
     */
    @PostMapping("/reload")
    public ResponseEntity<Map<String, Object>> reloadAllRules() {
        logger.info("Reloading all rules");
        
        try {
            rulesManagementService.reloadAllRules();
            
            List<RuleMetadata> rules = rulesManagementService.getLoadedRules();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All rules reloaded successfully");
            response.put("rulesLoaded", rules.size());
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Successfully reloaded {} rules", rules.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error reloading rules", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("RULES_RELOAD_ERROR", "Failed to reload rules: " + e.getMessage()));
        }
    }

    /**
     * Compile a rule without deploying it
     */
    @PostMapping("/compile")
    public ResponseEntity<Map<String, Object>> compileRule(@RequestBody Map<String, String> request) {
        String ruleContent = request.get("ruleContent");
        
        if (ruleContent == null || ruleContent.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_REQUEST", "Rule content is required"));
        }
        
        logger.debug("Compiling rule content");
        
        try {
            RuleCompilationResult result = rulesManagementService.compileRule(ruleContent);
            
            Map<String, Object> response = new HashMap<>();
            response.put("compilationResult", result);
            response.put("timestamp", LocalDateTime.now());
            
            if (result.isSuccess()) {
                logger.debug("Rule compilation successful");
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Rule compilation failed: {}", result.getErrors());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error compiling rule", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("COMPILATION_ERROR", "Failed to compile rule: " + e.getMessage()));
        }
    }

    /**
     * Deploy a new rule
     */
    @PostMapping("/deploy")
    public ResponseEntity<Map<String, Object>> deployRule(@RequestBody Map<String, String> request) {
        String ruleName = request.get("ruleName");
        String ruleContent = request.get("ruleContent");
        
        if (ruleName == null || ruleName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_REQUEST", "Rule name is required"));
        }
        
        if (ruleContent == null || ruleContent.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_REQUEST", "Rule content is required"));
        }
        
        logger.info("Deploying rule: {}", ruleName);
        
        try {
            rulesManagementService.deployRule(ruleName, ruleContent);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rule deployed successfully");
            response.put("ruleName", ruleName);
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Successfully deployed rule: {}", ruleName);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error deploying rule: {}", ruleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("DEPLOYMENT_ERROR", "Failed to deploy rule: " + e.getMessage()));
        }
    }

    /**
     * Remove a rule
     */
    @DeleteMapping("/{ruleName}")
    public ResponseEntity<Map<String, Object>> removeRule(@PathVariable String ruleName) {
        logger.info("Removing rule: {}", ruleName);
        
        try {
            rulesManagementService.removeRule(ruleName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rule removal initiated");
            response.put("ruleName", ruleName);
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Rule removal initiated for: {}", ruleName);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error removing rule: {}", ruleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("REMOVAL_ERROR", "Failed to remove rule: " + e.getMessage()));
        }
    }

    /**
     * Get detailed information about a specific rule
     */
    @GetMapping("/{ruleName}")
    public ResponseEntity<Map<String, Object>> getRuleDetails(@PathVariable String ruleName) {
        logger.debug("Getting details for rule: {}", ruleName);
        
        try {
            List<RuleMetadata> allRules = rulesManagementService.getLoadedRules();
            RuleMetadata ruleMetadata = allRules.stream()
                .filter(rule -> rule.getRuleName().equals(ruleName))
                .findFirst()
                .orElse(null);
            
            if (ruleMetadata == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("rule", ruleMetadata);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting rule details for: {}", ruleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("RULE_DETAILS_ERROR", "Failed to get rule details: " + e.getMessage()));
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
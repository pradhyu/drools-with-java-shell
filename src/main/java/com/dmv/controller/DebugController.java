package com.dmv.controller;

import com.dmv.model.*;
import com.dmv.service.DebugService;
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
 * REST controller for debugging and profiling operations
 */
@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    private final DebugService debugService;

    @Autowired
    public DebugController(DebugService debugService) {
        this.debugService = debugService;
    }

    /**
     * Create a new debug session
     */
    @PostMapping("/sessions")
    public ResponseEntity<Map<String, Object>> createDebugSession(@RequestBody Map<String, String> request) {
        String sessionName = request.get("sessionName");
        
        if (sessionName == null || sessionName.trim().isEmpty()) {
            sessionName = "Debug-" + LocalDateTime.now();
        }
        
        logger.info("Creating debug session: {}", sessionName);
        
        try {
            DebugSession session = debugService.createDebugSession(sessionName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("session", session);
            response.put("message", "Debug session created successfully");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error creating debug session: {}", sessionName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("SESSION_CREATION_ERROR", "Failed to create debug session: " + e.getMessage()));
        }
    }

    /**
     * Execute rules with detailed tracing
     */
    @PostMapping("/sessions/{sessionId}/trace")
    public ResponseEntity<Map<String, Object>> executeWithTrace(
            @PathVariable String sessionId, 
            @RequestBody List<Object> facts) {
        
        logger.info("Executing with trace in session: {}", sessionId);
        
        try {
            ExecutionTrace trace = debugService.executeWithTrace(sessionId, facts);
            
            Map<String, Object> response = new HashMap<>();
            response.put("trace", trace);
            response.put("sessionId", sessionId);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid debug session: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_SESSION", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error executing with trace in session: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("TRACE_EXECUTION_ERROR", "Failed to execute with trace: " + e.getMessage()));
        }
    }

    /**
     * Enable or disable step debugging
     */
    @PutMapping("/sessions/{sessionId}/step-debugging")
    public ResponseEntity<Map<String, Object>> setStepDebugging(
            @PathVariable String sessionId, 
            @RequestBody Map<String, Boolean> request) {
        
        Boolean enabled = request.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_REQUEST", "enabled field is required"));
        }
        
        logger.info("Setting step debugging to {} for session: {}", enabled, sessionId);
        
        try {
            debugService.setStepDebugging(sessionId, enabled);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Step debugging " + (enabled ? "enabled" : "disabled"));
            response.put("sessionId", sessionId);
            response.put("stepDebugging", enabled);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid debug session: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_SESSION", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error setting step debugging for session: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("STEP_DEBUG_ERROR", "Failed to set step debugging: " + e.getMessage()));
        }
    }

    /**
     * Add a breakpoint on a rule
     */
    @PostMapping("/sessions/{sessionId}/breakpoints")
    public ResponseEntity<Map<String, Object>> addRuleBreakpoint(
            @PathVariable String sessionId, 
            @RequestBody Map<String, String> request) {
        
        String ruleName = request.get("ruleName");
        if (ruleName == null || ruleName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_REQUEST", "Rule name is required"));
        }
        
        logger.info("Adding breakpoint on rule '{}' for session: {}", ruleName, sessionId);
        
        try {
            debugService.addRuleBreakpoint(sessionId, ruleName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Breakpoint added successfully");
            response.put("sessionId", sessionId);
            response.put("ruleName", ruleName);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid debug session: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_SESSION", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error adding breakpoint for session: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("BREAKPOINT_ERROR", "Failed to add breakpoint: " + e.getMessage()));
        }
    }

    /**
     * Remove a breakpoint from a rule
     */
    @DeleteMapping("/sessions/{sessionId}/breakpoints/{ruleName}")
    public ResponseEntity<Map<String, Object>> removeRuleBreakpoint(
            @PathVariable String sessionId, 
            @PathVariable String ruleName) {
        
        logger.info("Removing breakpoint on rule '{}' for session: {}", ruleName, sessionId);
        
        try {
            debugService.removeRuleBreakpoint(sessionId, ruleName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Breakpoint removed successfully");
            response.put("sessionId", sessionId);
            response.put("ruleName", ruleName);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid debug session: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_SESSION", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error removing breakpoint for session: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("BREAKPOINT_ERROR", "Failed to remove breakpoint: " + e.getMessage()));
        }
    }

    /**
     * Get current facts in a debug session
     */
    @GetMapping("/sessions/{sessionId}/facts")
    public ResponseEntity<Map<String, Object>> getCurrentFacts(@PathVariable String sessionId) {
        logger.debug("Getting current facts for session: {}", sessionId);
        
        try {
            List<Object> facts = debugService.getCurrentFacts(sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("facts", facts);
            response.put("count", facts.size());
            response.put("sessionId", sessionId);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid debug session: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_SESSION", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting current facts for session: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("FACTS_ERROR", "Failed to get current facts: " + e.getMessage()));
        }
    }

    /**
     * Get rule agenda for a debug session
     */
    @GetMapping("/sessions/{sessionId}/agenda")
    public ResponseEntity<Map<String, Object>> getRuleAgenda(@PathVariable String sessionId) {
        logger.debug("Getting rule agenda for session: {}", sessionId);
        
        try {
            List<String> agenda = debugService.getRuleAgenda(sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("agenda", agenda);
            response.put("count", agenda.size());
            response.put("sessionId", sessionId);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid debug session: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_SESSION", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting rule agenda for session: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("AGENDA_ERROR", "Failed to get rule agenda: " + e.getMessage()));
        }
    }

    /**
     * Analyze rule conflicts
     */
    @PostMapping("/analyze-conflicts")
    public ResponseEntity<Map<String, Object>> analyzeRuleConflicts(@RequestBody List<Object> facts) {
        logger.info("Analyzing rule conflicts for {} facts", facts.size());
        
        try {
            RuleConflictAnalysis analysis = debugService.analyzeRuleConflicts(facts);
            
            Map<String, Object> response = new HashMap<>();
            response.put("analysis", analysis);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error analyzing rule conflicts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("CONFLICT_ANALYSIS_ERROR", "Failed to analyze rule conflicts: " + e.getMessage()));
        }
    }

    /**
     * Get execution history for a debug session
     */
    @GetMapping("/sessions/{sessionId}/history")
    public ResponseEntity<Map<String, Object>> getExecutionHistory(@PathVariable String sessionId) {
        logger.debug("Getting execution history for session: {}", sessionId);
        
        try {
            List<ExecutionTrace> history = debugService.getExecutionHistory(sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("history", history);
            response.put("count", history.size());
            response.put("sessionId", sessionId);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid debug session: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_SESSION", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting execution history for session: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("HISTORY_ERROR", "Failed to get execution history: " + e.getMessage()));
        }
    }

    /**
     * Get all active debug sessions
     */
    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getActiveDebugSessions() {
        logger.debug("Getting active debug sessions");
        
        try {
            List<String> sessions = debugService.getActiveDebugSessions();
            
            Map<String, Object> response = new HashMap<>();
            response.put("sessions", sessions);
            response.put("count", sessions.size());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting active debug sessions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("SESSIONS_ERROR", "Failed to get active debug sessions: " + e.getMessage()));
        }
    }

    /**
     * Destroy a debug session
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Map<String, Object>> destroyDebugSession(@PathVariable String sessionId) {
        logger.info("Destroying debug session: {}", sessionId);
        
        try {
            debugService.destroyDebugSession(sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Debug session destroyed successfully");
            response.put("sessionId", sessionId);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error destroying debug session: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("SESSION_DESTROY_ERROR", "Failed to destroy debug session: " + e.getMessage()));
        }
    }

    /**
     * Get performance profiling information
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getPerformanceProfile() {
        logger.debug("Getting performance profile");
        
        try {
            Map<String, Object> profile = new HashMap<>();
            
            // JVM metrics
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> jvmMetrics = new HashMap<>();
            jvmMetrics.put("totalMemory", runtime.totalMemory());
            jvmMetrics.put("freeMemory", runtime.freeMemory());
            jvmMetrics.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
            jvmMetrics.put("maxMemory", runtime.maxMemory());
            jvmMetrics.put("availableProcessors", runtime.availableProcessors());
            
            profile.put("jvm", jvmMetrics);
            profile.put("activeDebugSessions", debugService.getActiveDebugSessions().size());
            profile.put("timestamp", LocalDateTime.now());
            
            Map<String, Object> response = new HashMap<>();
            response.put("profile", profile);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting performance profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("PROFILE_ERROR", "Failed to get performance profile: " + e.getMessage()));
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
package com.dmv.controller;

import com.dmv.model.RuleDiff;
import com.dmv.model.RuleVersion;
import com.dmv.service.VersionControlService;
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
 * REST controller for version control operations
 */
@RestController
@RequestMapping("/api/versions")
@CrossOrigin(origins = "*")
public class VersionControlController {

    private static final Logger logger = LoggerFactory.getLogger(VersionControlController.class);

    private final VersionControlService versionControlService;

    @Autowired
    public VersionControlController(VersionControlService versionControlService) {
        this.versionControlService = versionControlService;
    }

    /**
     * Get version history for a rule
     */
    @GetMapping("/{ruleName}/history")
    public ResponseEntity<Map<String, Object>> getRuleHistory(@PathVariable String ruleName) {
        logger.debug("Getting history for rule: {}", ruleName);
        
        try {
            List<RuleVersion> history = versionControlService.getRuleHistory(ruleName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("ruleName", ruleName);
            response.put("versions", history);
            response.put("count", history.size());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting rule history for: {}", ruleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("HISTORY_ERROR", "Failed to get rule history: " + e.getMessage()));
        }
    }

    /**
     * Get a specific version of a rule
     */
    @GetMapping("/{ruleName}/versions/{versionId}")
    public ResponseEntity<Map<String, Object>> getVersion(@PathVariable String ruleName, @PathVariable String versionId) {
        logger.debug("Getting version: {} for rule: {}", versionId, ruleName);
        
        try {
            RuleVersion version = versionControlService.getVersion(ruleName, versionId);
            
            if (version == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("version", version);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting version: {} for rule: {}", versionId, ruleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("VERSION_ERROR", "Failed to get version: " + e.getMessage()));
        }
    }

    /**
     * Get the latest version of a rule
     */
    @GetMapping("/{ruleName}/latest")
    public ResponseEntity<Map<String, Object>> getLatestVersion(@PathVariable String ruleName) {
        logger.debug("Getting latest version for rule: {}", ruleName);
        
        try {
            RuleVersion version = versionControlService.getLatestVersion(ruleName);
            
            if (version == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("version", version);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting latest version for rule: {}", ruleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("LATEST_VERSION_ERROR", "Failed to get latest version: " + e.getMessage()));
        }
    }

    /**
     * Save a new version of a rule
     */
    @PostMapping("/{ruleName}/versions")
    public ResponseEntity<Map<String, Object>> saveVersion(@PathVariable String ruleName, @RequestBody Map<String, String> request) {
        String content = request.get("content");
        String author = request.get("author");
        String commitMessage = request.get("commitMessage");
        
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_REQUEST", "Rule content is required"));
        }
        
        if (author == null || author.trim().isEmpty()) {
            author = "unknown";
        }
        
        logger.info("Saving new version of rule: {} by author: {}", ruleName, author);
        
        try {
            RuleVersion version = versionControlService.saveRuleVersion(ruleName, content, author, commitMessage);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Version saved successfully");
            response.put("version", version);
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Successfully saved version: {} for rule: {}", version.getVersionId(), ruleName);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error saving version for rule: {}", ruleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("SAVE_VERSION_ERROR", "Failed to save version: " + e.getMessage()));
        }
    }

    /**
     * Compare two versions of a rule
     */
    @GetMapping("/{ruleName}/compare/{version1}/{version2}")
    public ResponseEntity<Map<String, Object>> compareVersions(
            @PathVariable String ruleName,
            @PathVariable String version1,
            @PathVariable String version2) {
        
        logger.debug("Comparing versions {} and {} for rule: {}", version1, version2, ruleName);
        
        try {
            RuleDiff diff = versionControlService.compareVersions(ruleName, version1, version2);
            
            Map<String, Object> response = new HashMap<>();
            response.put("diff", diff);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid version comparison request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_VERSION", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error comparing versions for rule: {}", ruleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("COMPARE_ERROR", "Failed to compare versions: " + e.getMessage()));
        }
    }

    /**
     * Rollback a rule to a specific version
     */
    @PostMapping("/{ruleName}/rollback/{versionId}")
    public ResponseEntity<Map<String, Object>> rollbackToVersion(@PathVariable String ruleName, @PathVariable String versionId) {
        logger.info("Rolling back rule: {} to version: {}", ruleName, versionId);
        
        try {
            RuleVersion rollbackVersion = versionControlService.rollbackToVersion(ruleName, versionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully rolled back to version " + versionId);
            response.put("newVersion", rollbackVersion);
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Successfully rolled back rule: {} to version: {}", ruleName, versionId);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid rollback request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_VERSION", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error rolling back rule: {} to version: {}", ruleName, versionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("ROLLBACK_ERROR", "Failed to rollback: " + e.getMessage()));
        }
    }

    /**
     * Tag a specific version
     */
    @PostMapping("/{ruleName}/versions/{versionId}/tags")
    public ResponseEntity<Map<String, Object>> tagVersion(
            @PathVariable String ruleName,
            @PathVariable String versionId,
            @RequestBody Map<String, String> request) {
        
        String tag = request.get("tag");
        
        if (tag == null || tag.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_REQUEST", "Tag is required"));
        }
        
        logger.info("Tagging version: {} of rule: {} with tag: {}", versionId, ruleName, tag);
        
        try {
            versionControlService.tagVersion(ruleName, versionId, tag);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Version tagged successfully");
            response.put("ruleName", ruleName);
            response.put("versionId", versionId);
            response.put("tag", tag);
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Successfully tagged version: {} with tag: {}", versionId, tag);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid tag request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_VERSION", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error tagging version: {} of rule: {}", versionId, ruleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("TAG_ERROR", "Failed to tag version: " + e.getMessage()));
        }
    }

    /**
     * Get all versioned rules
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getVersionedRules() {
        logger.debug("Getting all versioned rules");
        
        try {
            List<String> ruleNames = versionControlService.getVersionedRules();
            
            Map<String, Object> response = new HashMap<>();
            response.put("rules", ruleNames);
            response.put("count", ruleNames.size());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting versioned rules", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("VERSIONED_RULES_ERROR", "Failed to get versioned rules: " + e.getMessage()));
        }
    }

    /**
     * Delete a specific version
     */
    @DeleteMapping("/{ruleName}/versions/{versionId}")
    public ResponseEntity<Map<String, Object>> deleteVersion(@PathVariable String ruleName, @PathVariable String versionId) {
        logger.info("Deleting version: {} of rule: {}", versionId, ruleName);
        
        try {
            versionControlService.deleteVersion(ruleName, versionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Version deleted successfully");
            response.put("ruleName", ruleName);
            response.put("versionId", versionId);
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Successfully deleted version: {} of rule: {}", versionId, ruleName);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid delete request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_VERSION", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting version: {} of rule: {}", versionId, ruleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("DELETE_ERROR", "Failed to delete version: " + e.getMessage()));
        }
    }

    /**
     * Delete all versions of a rule
     */
    @DeleteMapping("/{ruleName}/history")
    public ResponseEntity<Map<String, Object>> deleteRuleHistory(@PathVariable String ruleName) {
        logger.info("Deleting all history for rule: {}", ruleName);
        
        try {
            versionControlService.deleteRuleHistory(ruleName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rule history deleted successfully");
            response.put("ruleName", ruleName);
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Successfully deleted all history for rule: {}", ruleName);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error deleting history for rule: {}", ruleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("DELETE_HISTORY_ERROR", "Failed to delete rule history: " + e.getMessage()));
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
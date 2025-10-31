package com.dmv.service;

import com.dmv.model.RuleDiff;
import com.dmv.model.RuleVersion;

import java.util.List;

/**
 * Service interface for rule version control and management
 */
public interface VersionControlService {
    
    /**
     * Save a new version of a rule
     * @param ruleName Name of the rule
     * @param content Rule content
     * @param author Author of the change
     * @return Created rule version
     */
    RuleVersion saveRuleVersion(String ruleName, String content, String author);
    
    /**
     * Save a new version of a rule with commit message
     * @param ruleName Name of the rule
     * @param content Rule content
     * @param author Author of the change
     * @param commitMessage Commit message describing the change
     * @return Created rule version
     */
    RuleVersion saveRuleVersion(String ruleName, String content, String author, String commitMessage);
    
    /**
     * Get version history for a rule
     * @param ruleName Name of the rule
     * @return List of rule versions ordered by timestamp (newest first)
     */
    List<RuleVersion> getRuleHistory(String ruleName);
    
    /**
     * Rollback a rule to a specific version
     * @param ruleName Name of the rule
     * @param versionId Version ID to rollback to
     * @return The version that was rolled back to
     */
    RuleVersion rollbackToVersion(String ruleName, String versionId);
    
    /**
     * Compare two versions of a rule
     * @param ruleName Name of the rule
     * @param version1 First version ID
     * @param version2 Second version ID
     * @return Diff between the two versions
     */
    RuleDiff compareVersions(String ruleName, String version1, String version2);
    
    /**
     * Tag a specific version
     * @param ruleName Name of the rule
     * @param versionId Version ID to tag
     * @param tag Tag name
     */
    void tagVersion(String ruleName, String versionId, String tag);
    
    /**
     * Get a specific version of a rule
     * @param ruleName Name of the rule
     * @param versionId Version ID
     * @return Rule version or null if not found
     */
    RuleVersion getVersion(String ruleName, String versionId);
    
    /**
     * Get the latest version of a rule
     * @param ruleName Name of the rule
     * @return Latest rule version or null if not found
     */
    RuleVersion getLatestVersion(String ruleName);
    
    /**
     * Get all rules that have version history
     * @return List of rule names with version history
     */
    List<String> getVersionedRules();
    
    /**
     * Delete all versions of a rule
     * @param ruleName Name of the rule
     */
    void deleteRuleHistory(String ruleName);
    
    /**
     * Delete a specific version of a rule
     * @param ruleName Name of the rule
     * @param versionId Version ID to delete
     */
    void deleteVersion(String ruleName, String versionId);
}
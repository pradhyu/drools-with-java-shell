package com.dmv.service.impl;

import com.dmv.model.RuleDiff;
import com.dmv.model.RuleVersion;
import com.dmv.repository.RuleVersionRepository;
import com.dmv.service.VersionControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VersionControlServiceImpl implements VersionControlService {

    private static final Logger logger = LoggerFactory.getLogger(VersionControlServiceImpl.class);

    private final RuleVersionRepository ruleVersionRepository;

    @Autowired
    public VersionControlServiceImpl(RuleVersionRepository ruleVersionRepository) {
        this.ruleVersionRepository = ruleVersionRepository;
    }

    @Override
    public RuleVersion saveRuleVersion(String ruleName, String content, String author) {
        return saveRuleVersion(ruleName, content, author, null);
    }

    @Override
    public RuleVersion saveRuleVersion(String ruleName, String content, String author, String commitMessage) {
        logger.info("Saving new version of rule: {} by author: {}", ruleName, author);
        
        String versionId = generateVersionId();
        RuleVersion ruleVersion = new RuleVersion(versionId, ruleName, content, author, commitMessage);
        
        RuleVersion savedVersion = ruleVersionRepository.save(ruleVersion);
        
        logger.info("Saved rule version: {} for rule: {}", versionId, ruleName);
        return savedVersion;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleVersion> getRuleHistory(String ruleName) {
        logger.debug("Getting history for rule: {}", ruleName);
        
        List<RuleVersion> history = ruleVersionRepository.findByRuleNameOrderByTimestampDesc(ruleName);
        
        logger.debug("Retrieved {} versions for rule: {}", history.size(), ruleName);
        return history;
    }

    @Override
    public RuleVersion rollbackToVersion(String ruleName, String versionId) {
        logger.info("Rolling back rule: {} to version: {}", ruleName, versionId);
        
        RuleVersion targetVersion = ruleVersionRepository.findByRuleNameAndVersionId(ruleName, versionId)
            .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId));
        
        // Create a new version with the content from the target version
        String rollbackCommitMessage = "Rollback to version " + versionId;
        RuleVersion rollbackVersion = saveRuleVersion(
            ruleName, 
            targetVersion.getContent(), 
            "system", 
            rollbackCommitMessage
        );
        
        rollbackVersion.addTag("rollback");
        ruleVersionRepository.save(rollbackVersion);
        
        logger.info("Successfully rolled back rule: {} to version: {}, created new version: {}", 
                   ruleName, versionId, rollbackVersion.getVersionId());
        
        return rollbackVersion;
    }

    @Override
    @Transactional(readOnly = true)
    public RuleDiff compareVersions(String ruleName, String version1, String version2) {
        logger.debug("Comparing versions {} and {} for rule: {}", version1, version2, ruleName);
        
        RuleVersion v1 = ruleVersionRepository.findByRuleNameAndVersionId(ruleName, version1)
            .orElseThrow(() -> new IllegalArgumentException("Version not found: " + version1));
        
        RuleVersion v2 = ruleVersionRepository.findByRuleNameAndVersionId(ruleName, version2)
            .orElseThrow(() -> new IllegalArgumentException("Version not found: " + version2));
        
        RuleDiff diff = createDiff(v1, v2);
        
        logger.debug("Generated diff for rule: {} between versions {} and {}", ruleName, version1, version2);
        return diff;
    }

    @Override
    public void tagVersion(String ruleName, String versionId, String tag) {
        logger.info("Tagging version: {} of rule: {} with tag: {}", versionId, ruleName, tag);
        
        RuleVersion version = ruleVersionRepository.findByRuleNameAndVersionId(ruleName, versionId)
            .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId));
        
        version.addTag(tag);
        ruleVersionRepository.save(version);
        
        logger.info("Successfully tagged version: {} with tag: {}", versionId, tag);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleVersion getVersion(String ruleName, String versionId) {
        logger.debug("Getting version: {} for rule: {}", versionId, ruleName);
        
        return ruleVersionRepository.findByRuleNameAndVersionId(ruleName, versionId)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleVersion getLatestVersion(String ruleName) {
        logger.debug("Getting latest version for rule: {}", ruleName);
        
        return ruleVersionRepository.findLatestVersionByRuleName(ruleName)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getVersionedRules() {
        logger.debug("Getting all versioned rules");
        
        List<String> ruleNames = ruleVersionRepository.findDistinctRuleNames();
        
        logger.debug("Found {} versioned rules", ruleNames.size());
        return ruleNames;
    }

    @Override
    public void deleteRuleHistory(String ruleName) {
        logger.info("Deleting all history for rule: {}", ruleName);
        
        ruleVersionRepository.deleteByRuleName(ruleName);
        
        logger.info("Successfully deleted all history for rule: {}", ruleName);
    }

    @Override
    public void deleteVersion(String ruleName, String versionId) {
        logger.info("Deleting version: {} of rule: {}", versionId, ruleName);
        
        RuleVersion version = ruleVersionRepository.findByRuleNameAndVersionId(ruleName, versionId)
            .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId));
        
        // Mark as inactive instead of deleting to preserve history integrity
        version.setActive(false);
        ruleVersionRepository.save(version);
        
        logger.info("Successfully marked version: {} as inactive", versionId);
    }

    private String generateVersionId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private RuleDiff createDiff(RuleVersion v1, RuleVersion v2) {
        RuleDiff diff = new RuleDiff(v1.getRuleName(), v1.getVersionId(), v2.getVersionId());
        diff.setVersion1Timestamp(v1.getTimestamp());
        diff.setVersion2Timestamp(v2.getTimestamp());
        diff.setVersion1Author(v1.getAuthor());
        diff.setVersion2Author(v2.getAuthor());
        
        // Simple line-by-line diff implementation
        String[] lines1 = v1.getContent().split("\n");
        String[] lines2 = v2.getContent().split("\n");
        
        int linesAdded = 0;
        int linesRemoved = 0;
        int linesModified = 0;
        
        // Basic diff algorithm - could be enhanced with more sophisticated algorithms
        int maxLines = Math.max(lines1.length, lines2.length);
        
        for (int i = 0; i < maxLines; i++) {
            String line1 = i < lines1.length ? lines1[i] : null;
            String line2 = i < lines2.length ? lines2[i] : null;
            
            if (line1 == null && line2 != null) {
                // Line added
                diff.addDiffLine(new RuleDiff.DiffLine(RuleDiff.DiffType.ADDED, i + 1, line2));
                linesAdded++;
            } else if (line1 != null && line2 == null) {
                // Line removed
                diff.addDiffLine(new RuleDiff.DiffLine(RuleDiff.DiffType.REMOVED, i + 1, line1));
                linesRemoved++;
            } else if (line1 != null && line2 != null) {
                if (!line1.equals(line2)) {
                    // Line modified
                    diff.addDiffLine(new RuleDiff.DiffLine(RuleDiff.DiffType.REMOVED, i + 1, line1));
                    diff.addDiffLine(new RuleDiff.DiffLine(RuleDiff.DiffType.ADDED, i + 1, line2));
                    linesModified++;
                } else {
                    // Line unchanged
                    diff.addDiffLine(new RuleDiff.DiffLine(RuleDiff.DiffType.UNCHANGED, i + 1, line1));
                }
            }
        }
        
        diff.setStatistics(new RuleDiff.DiffStatistics(linesAdded, linesRemoved, linesModified));
        
        return diff;
    }
}
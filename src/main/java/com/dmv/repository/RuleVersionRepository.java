package com.dmv.repository;

import com.dmv.model.RuleVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleVersionRepository extends JpaRepository<RuleVersion, Long> {
    
    /**
     * Find all versions of a rule ordered by timestamp descending
     */
    List<RuleVersion> findByRuleNameOrderByTimestampDesc(String ruleName);
    
    /**
     * Find a specific version by rule name and version ID
     */
    Optional<RuleVersion> findByRuleNameAndVersionId(String ruleName, String versionId);
    
    /**
     * Find the latest version of a rule
     */
    @Query("SELECT rv FROM RuleVersion rv WHERE rv.ruleName = :ruleName AND rv.active = true ORDER BY rv.timestamp DESC")
    Optional<RuleVersion> findLatestVersionByRuleName(@Param("ruleName") String ruleName);
    
    /**
     * Find all distinct rule names that have versions
     */
    @Query("SELECT DISTINCT rv.ruleName FROM RuleVersion rv WHERE rv.active = true")
    List<String> findDistinctRuleNames();
    
    /**
     * Find versions by rule name and tag
     */
    @Query("SELECT rv FROM RuleVersion rv JOIN rv.tags t WHERE rv.ruleName = :ruleName AND t = :tag")
    List<RuleVersion> findByRuleNameAndTag(@Param("ruleName") String ruleName, @Param("tag") String tag);
    
    /**
     * Count versions for a rule
     */
    long countByRuleNameAndActiveTrue(String ruleName);
    
    /**
     * Delete all versions of a rule
     */
    void deleteByRuleName(String ruleName);
    
    /**
     * Find versions by author
     */
    List<RuleVersion> findByAuthorOrderByTimestampDesc(String author);
}
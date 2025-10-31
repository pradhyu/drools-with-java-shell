package com.dmv.service;

import com.dmv.model.RuleCompilationResult;
import com.dmv.model.RuleExecutionResult;
import com.dmv.model.RuleMetadata;

import java.util.List;

/**
 * Service interface for managing Drools rules lifecycle, compilation, and execution
 */
public interface RulesManagementService {
    
    /**
     * Compile a rule from string content
     * @param ruleContent The rule content in DRL format
     * @return Compilation result with success/failure and error messages
     */
    RuleCompilationResult compileRule(String ruleContent);
    
    /**
     * Deploy a rule to the active rule base
     * @param ruleName Name of the rule
     * @param ruleContent Rule content in DRL format
     */
    void deployRule(String ruleName, String ruleContent);
    
    /**
     * Reload all rules from the rules directory
     */
    void reloadAllRules();
    
    /**
     * Get metadata for all currently loaded rules
     * @return List of rule metadata
     */
    List<RuleMetadata> getLoadedRules();
    
    /**
     * Execute rules against provided facts
     * @param facts List of fact objects to evaluate
     * @return Execution result with fired rules and modified facts
     */
    RuleExecutionResult executeRules(List<Object> facts);
    
    /**
     * Remove a rule from the active rule base
     * @param ruleName Name of the rule to remove
     */
    void removeRule(String ruleName);
    
    /**
     * Get the current KieContainer for direct access
     * @return Current KieContainer instance
     */
    org.kie.api.runtime.KieContainer getKieContainer();
}
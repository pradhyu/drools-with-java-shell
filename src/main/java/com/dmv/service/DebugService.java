package com.dmv.service;

import com.dmv.model.DebugSession;
import com.dmv.model.ExecutionTrace;
import com.dmv.model.RuleConflictAnalysis;

import java.util.List;

/**
 * Service interface for rule debugging and profiling
 */
public interface DebugService {
    
    /**
     * Create a new debug session
     * @param sessionName Name for the debug session
     * @return Created debug session
     */
    DebugSession createDebugSession(String sessionName);
    
    /**
     * Execute rules with detailed tracing
     * @param sessionId Debug session ID
     * @param facts List of fact objects
     * @return Detailed execution trace
     */
    ExecutionTrace executeWithTrace(String sessionId, List<Object> facts);
    
    /**
     * Enable step-by-step debugging
     * @param sessionId Debug session ID
     * @param enabled Whether to enable step debugging
     */
    void setStepDebugging(String sessionId, boolean enabled);
    
    /**
     * Add a breakpoint on a specific rule
     * @param sessionId Debug session ID
     * @param ruleName Name of the rule to break on
     */
    void addRuleBreakpoint(String sessionId, String ruleName);
    
    /**
     * Remove a breakpoint
     * @param sessionId Debug session ID
     * @param ruleName Name of the rule to remove breakpoint from
     */
    void removeRuleBreakpoint(String sessionId, String ruleName);
    
    /**
     * Get current fact state in a debug session
     * @param sessionId Debug session ID
     * @return List of current facts
     */
    List<Object> getCurrentFacts(String sessionId);
    
    /**
     * Get rule agenda (rules ready to fire)
     * @param sessionId Debug session ID
     * @return List of rules in the agenda
     */
    List<String> getRuleAgenda(String sessionId);
    
    /**
     * Analyze rule conflicts
     * @param facts List of fact objects
     * @return Rule conflict analysis
     */
    RuleConflictAnalysis analyzeRuleConflicts(List<Object> facts);
    
    /**
     * Get execution history for a debug session
     * @param sessionId Debug session ID
     * @return List of execution traces
     */
    List<ExecutionTrace> getExecutionHistory(String sessionId);
    
    /**
     * Destroy a debug session
     * @param sessionId Debug session ID
     */
    void destroyDebugSession(String sessionId);
    
    /**
     * Get all active debug sessions
     * @return List of active session IDs
     */
    List<String> getActiveDebugSessions();
}
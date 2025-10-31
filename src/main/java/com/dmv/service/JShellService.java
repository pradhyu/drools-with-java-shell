package com.dmv.service;

import com.dmv.model.CompletionSuggestion;
import com.dmv.model.ExecutionResult;
import com.dmv.model.JShellSession;

import java.util.List;

/**
 * Service interface for JShell integration and management
 */
public interface JShellService {
    
    /**
     * Create a new JShell session
     * @param sessionId Unique identifier for the session
     * @return Created JShell session
     */
    JShellSession createSession(String sessionId);
    
    /**
     * Execute Java code in a specific session
     * @param sessionId Session identifier
     * @param code Java code to execute
     * @return Execution result with output and status
     */
    ExecutionResult executeCode(String sessionId, String code);
    
    /**
     * Preload Drools imports and common classes into a session
     * @param session JShell session to configure
     */
    void preloadDroolsImports(JShellSession session);
    
    /**
     * Get code completion suggestions for partial input
     * @param sessionId Session identifier
     * @param partial Partial code input
     * @return List of completion suggestions
     */
    List<CompletionSuggestion> getCompletions(String sessionId, String partial);
    
    /**
     * Destroy a JShell session and clean up resources
     * @param sessionId Session identifier
     */
    void destroySession(String sessionId);
    
    /**
     * Get all active sessions
     * @return List of active session IDs
     */
    List<String> getActiveSessions();
    
    /**
     * Check if a session exists and is active
     * @param sessionId Session identifier
     * @return true if session exists and is active
     */
    boolean isSessionActive(String sessionId);
    
    /**
     * Get session information
     * @param sessionId Session identifier
     * @return Session information or null if not found
     */
    JShellSession getSession(String sessionId);
}
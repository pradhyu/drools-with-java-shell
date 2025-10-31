package com.dmv.service;

import com.dmv.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JShellServiceTest {

    @Test
    void testExecutionResultStructure() {
        ExecutionResult result = new ExecutionResult();
        result.setSuccess(true);
        result.setResultValue("42");
        result.setResultType("int");
        result.setOutput("42");
        result.setExecutionTimeMs(100L);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("42", result.getResultValue());
        assertEquals("int", result.getResultType());
        assertEquals("42", result.getOutput());
        assertEquals(100L, result.getExecutionTimeMs());
    }

    @Test
    void testExecutionResultFailure() {
        ExecutionResult result = new ExecutionResult();
        result.setSuccess(false);
        result.setErrorOutput("Compilation error");
        result.addDiagnostic("Syntax error on line 1");
        
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Compilation error", result.getErrorOutput());
        assertFalse(result.getDiagnostics().isEmpty());
        assertTrue(result.getDiagnostics().contains("Syntax error on line 1"));
    }

    @Test
    void testExecutionResultTiming() {
        ExecutionResult result = new ExecutionResult();
        long startTime = System.currentTimeMillis();
        
        // Simulate some processing time
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.currentTimeMillis();
        result.setExecutionTimeMs(endTime - startTime);
        
        assertTrue(result.getExecutionTimeMs() >= 10);
    }

    @Test
    void testJShellSessionStructure() {
        JShellSession session = new JShellSession("test-session", null);
        
        assertNotNull(session);
        assertEquals("test-session", session.getSessionId());
        assertTrue(session.isActive());
        assertNotNull(session.getExecutionHistory());
        assertTrue(session.getExecutionHistory().isEmpty());
    }

    @Test
    void testCompletionSuggestionStructure() {
        CompletionSuggestion suggestion = new CompletionSuggestion();
        suggestion.setSuggestion("System.out.println");
        suggestion.setDescription("Print to console");
        suggestion.setType("method");
        
        assertNotNull(suggestion);
        assertEquals("System.out.println", suggestion.getSuggestion());
        assertEquals("Print to console", suggestion.getDescription());
        assertEquals("method", suggestion.getType());
    }
}
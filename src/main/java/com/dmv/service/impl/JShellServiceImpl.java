package com.dmv.service.impl;

import com.dmv.model.CompletionSuggestion;
import com.dmv.model.ExecutionResult;
import com.dmv.model.JShellSession;
import com.dmv.service.JShellService;
import com.dmv.service.RulesManagementService;
import jdk.jshell.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class JShellServiceImpl implements JShellService {

    private static final Logger logger = LoggerFactory.getLogger(JShellServiceImpl.class);
    private static final long SESSION_TIMEOUT_MINUTES = 60;

    private final ConcurrentHashMap<String, JShellSession> sessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor = Executors.newScheduledThreadPool(1);
    private final ApplicationContext applicationContext;
    private final RulesManagementService rulesManagementService;

    @Autowired
    public JShellServiceImpl(ApplicationContext applicationContext, RulesManagementService rulesManagementService) {
        this.applicationContext = applicationContext;
        this.rulesManagementService = rulesManagementService;
        
        // Start cleanup task for expired sessions
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredSessions, 10, 10, TimeUnit.MINUTES);
        
        logger.info("JShell service initialized");
    }

    @Override
    public JShellSession createSession(String sessionId) {
        logger.info("Creating JShell session: {}", sessionId);
        
        try {
            // Create JShell instance with custom configuration
            JShell jshell = JShell.builder()
                .out(new PrintStream(new ByteArrayOutputStream())) // Capture output
                .err(new PrintStream(new ByteArrayOutputStream())) // Capture errors
                .build();
            
            JShellSession session = new JShellSession(sessionId, jshell);
            
            // Preload Drools imports and common classes
            preloadDroolsImports(session);
            
            sessions.put(sessionId, session);
            
            logger.info("JShell session created successfully: {}", sessionId);
            return session;
            
        } catch (Exception e) {
            logger.error("Failed to create JShell session: {}", sessionId, e);
            throw new RuntimeException("Failed to create JShell session: " + e.getMessage(), e);
        }
    }

    @Override
    public ExecutionResult executeCode(String sessionId, String code) {
        logger.debug("Executing code in session: {}", sessionId);
        
        JShellSession session = sessions.get(sessionId);
        if (session == null || !session.isActive()) {
            throw new IllegalArgumentException("Session not found or inactive: " + sessionId);
        }
        
        ExecutionResult result = new ExecutionResult();
        long startTime = System.currentTimeMillis();
        
        try {
            JShell jshell = session.getJshell();
            
            // Capture output streams
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            
            // Execute the code
            List<SnippetEvent> events = jshell.eval(code);
            
            StringBuilder output = new StringBuilder();
            StringBuilder errors = new StringBuilder();
            
            for (SnippetEvent event : events) {
                Snippet snippet = event.snippet();
                
                if (event.status() == Snippet.Status.VALID) {
                    result.setSuccess(true);
                    
                    // Get the result value if available
                    if (event.value() != null) {
                        result.setResultValue(event.value());
                        output.append(event.value()).append("\n");
                    }
                    
                    // Set result type
                    if (snippet.kind() == Snippet.Kind.EXPRESSION) {
                        VarSnippet varSnippet = (VarSnippet) snippet;
                        result.setResultType(varSnippet.typeName());
                    }
                    
                } else if (event.status() == Snippet.Status.REJECTED) {
                    result.setSuccess(false);
                    
                    // Collect diagnostics
                    jshell.diagnostics(snippet).forEach(diagnostic -> {
                        String diagMessage = diagnostic.getMessage(null);
                        result.addDiagnostic(diagMessage);
                        errors.append(diagMessage).append("\n");
                    });
                }
            }
            
            result.setOutput(output.toString());
            result.setErrorOutput(errors.toString());
            
            // Add to session history
            session.addToHistory(code);
            
            logger.debug("Code execution completed for session: {}", sessionId);
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorOutput("Execution error: " + e.getMessage());
            logger.error("Error executing code in session: {}", sessionId, e);
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            result.setExecutionTimeMs(executionTime);
        }
        
        return result;
    }

    @Override
    public void preloadDroolsImports(JShellSession session) {
        logger.debug("Preloading Drools imports for session: {}", session.getSessionId());
        
        List<String> imports = List.of(
            "import org.kie.api.runtime.KieSession;",
            "import org.kie.api.runtime.KieContainer;",
            "import org.drools.core.impl.StatefulKnowledgeSessionImpl;",
            "import com.dmv.model.*;",
            "import com.dmv.model.builder.*;",
            "import com.dmv.service.*;",
            "import java.time.LocalDate;",
            "import java.math.BigDecimal;",
            "import java.util.*;",
            "import java.util.stream.*;"
        );
        
        JShell jshell = session.getJshell();
        
        for (String importStatement : imports) {
            try {
                jshell.eval(importStatement);
                logger.debug("Imported: {}", importStatement);
            } catch (Exception e) {
                logger.warn("Failed to import: {} - {}", importStatement, e.getMessage());
            }
        }
        
        // Add helper methods for JSON conversion and Spring beans access
        try {
            // Add JSON conversion helper methods
            jshell.eval("String jsonToFact(String json) { " +
                       "return com.dmv.service.JsonFactsConverterService.class.cast(" +
                       "org.springframework.context.ApplicationContext.class.cast(null)" +
                       ".getBean(com.dmv.service.JsonFactsConverterService.class))" +
                       ".jsonToLicenseRenewalRequest(json); }");
            
            jshell.eval("String factToJson(Object fact) { " +
                       "return com.dmv.service.JsonFactsConverterService.class.cast(" +
                       "org.springframework.context.ApplicationContext.class.cast(null)" +
                       ".getBean(com.dmv.service.JsonFactsConverterService.class))" +
                       ".factToJson(fact); }");
            
            // Add sample data builders
            jshell.eval("var sampleAdult = com.dmv.model.builder.LicenseRenewalRequestBuilder.createValidAdultRenewal();");
            jshell.eval("var sampleMinor = com.dmv.model.builder.LicenseRenewalRequestBuilder.createMinorRenewal();");
            jshell.eval("var sampleExpired = com.dmv.model.builder.LicenseRenewalRequestBuilder.createExpiredLicenseRenewal();");
            jshell.eval("var sampleWithViolations = com.dmv.model.builder.LicenseRenewalRequestBuilder.createRenewalWithViolations();");
            
            logger.debug("Added helper methods and sample data");
        } catch (Exception e) {
            logger.warn("Failed to add helper methods", e);
        }
        
        logger.debug("Drools imports preloaded for session: {}", session.getSessionId());
    }

    @Override
    public List<CompletionSuggestion> getCompletions(String sessionId, String partial) {
        logger.debug("Getting completions for session: {} with partial: {}", sessionId, partial);
        
        JShellSession session = sessions.get(sessionId);
        if (session == null || !session.isActive()) {
            return new ArrayList<>();
        }
        
        List<CompletionSuggestion> suggestions = new ArrayList<>();
        
        try {
            JShell jshell = session.getJshell();
            SourceCodeAnalysis sourceAnalysis = jshell.sourceCodeAnalysis();
            
            // Simplified completion for compatibility
            // JShell completion API may vary by version
            try {
                var jshellSuggestions = sourceAnalysis.completionSuggestions(partial, partial.length(), new int[1]);
                
                for (var suggestion : jshellSuggestions) {
                    CompletionSuggestion completionSuggestion = new CompletionSuggestion();
                    completionSuggestion.setSuggestion(suggestion.continuation());
                    completionSuggestion.setDescription(suggestion.continuation());
                    completionSuggestion.setType("method"); // Default type
                    
                    suggestions.add(completionSuggestion);
                }
            } catch (Exception e) {
                logger.warn("JShell completion not available: {}", e.getMessage());
                // Add basic completions
                suggestions.add(new CompletionSuggestion("sampleAdult", "Sample adult renewal request", "variable"));
                suggestions.add(new CompletionSuggestion("sampleMinor", "Sample minor renewal request", "variable"));
            }
            
            logger.debug("Generated {} completion suggestions", suggestions.size());
            
        } catch (Exception e) {
            logger.error("Error getting completions for session: {}", sessionId, e);
        }
        
        return suggestions;
    }

    @Override
    public void destroySession(String sessionId) {
        logger.info("Destroying JShell session: {}", sessionId);
        
        JShellSession session = sessions.remove(sessionId);
        if (session != null) {
            session.close();
            logger.info("JShell session destroyed: {}", sessionId);
        } else {
            logger.warn("Session not found for destruction: {}", sessionId);
        }
    }

    @Override
    public List<String> getActiveSessions() {
        return new ArrayList<>(sessions.keySet());
    }

    @Override
    public boolean isSessionActive(String sessionId) {
        JShellSession session = sessions.get(sessionId);
        return session != null && session.isActive();
    }

    @Override
    public JShellSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    private void cleanupExpiredSessions() {
        logger.debug("Running session cleanup task");
        
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(SESSION_TIMEOUT_MINUTES);
        List<String> expiredSessions = new ArrayList<>();
        
        for (JShellSession session : sessions.values()) {
            if (session.getLastAccessedAt().isBefore(cutoff)) {
                expiredSessions.add(session.getSessionId());
            }
        }
        
        for (String sessionId : expiredSessions) {
            logger.info("Cleaning up expired session: {}", sessionId);
            destroySession(sessionId);
        }
        
        if (!expiredSessions.isEmpty()) {
            logger.info("Cleaned up {} expired sessions", expiredSessions.size());
        }
    }
}
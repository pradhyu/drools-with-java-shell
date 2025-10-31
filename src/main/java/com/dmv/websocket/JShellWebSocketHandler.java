package com.dmv.websocket;

import com.dmv.model.ExecutionResult;
import com.dmv.model.JShellSession;
import com.dmv.service.JShellService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JShellWebSocketHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(JShellWebSocketHandler.class);
    
    private final JShellService jshellService;
    private final ObjectMapper objectMapper;
    private final Map<String, String> sessionToJShellMapping = new ConcurrentHashMap<>();

    @Autowired
    public JShellWebSocketHandler(JShellService jshellService) {
        this.jshellService = jshellService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocket connection established: {}", session.getId());
        
        // Create JShell session for this WebSocket connection
        String jshellSessionId = "ws-" + session.getId();
        JShellSession jshellSession = jshellService.createSession(jshellSessionId);
        sessionToJShellMapping.put(session.getId(), jshellSessionId);
        
        // Send welcome message
        sendMessage(session, Map.of(
            "type", "welcome",
            "message", "JShell session created successfully",
            "sessionId", jshellSessionId,
            "timestamp", java.time.LocalDateTime.now()
        ));
        
        logger.info("JShell session created for WebSocket: {} -> {}", session.getId(), jshellSessionId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            handleTextMessage(session, (TextMessage) message);
        }
    }

    private void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        logger.debug("Received message from WebSocket {}: {}", session.getId(), payload);
        
        try {
            Map<String, Object> request = objectMapper.readValue(payload, Map.class);
            String type = (String) request.get("type");
            
            switch (type) {
                case "execute":
                    handleExecuteRequest(session, request);
                    break;
                    
                case "complete":
                    handleCompletionRequest(session, request);
                    break;
                    
                case "history":
                    handleHistoryRequest(session, request);
                    break;
                    
                default:
                    sendErrorMessage(session, "Unknown message type: " + type);
            }
            
        } catch (Exception e) {
            logger.error("Error handling WebSocket message", e);
            sendErrorMessage(session, "Error processing message: " + e.getMessage());
        }
    }

    private void handleExecuteRequest(WebSocketSession session, Map<String, Object> request) throws IOException {
        String code = (String) request.get("code");
        String jshellSessionId = sessionToJShellMapping.get(session.getId());
        
        if (jshellSessionId == null) {
            sendErrorMessage(session, "JShell session not found");
            return;
        }
        
        try {
            ExecutionResult result = jshellService.executeCode(jshellSessionId, code);
            
            sendMessage(session, Map.of(
                "type", "execution_result",
                "success", result.isSuccess(),
                "output", result.getOutput() != null ? result.getOutput() : "",
                "errorOutput", result.getErrorOutput() != null ? result.getErrorOutput() : "",
                "resultValue", result.getResultValue() != null ? result.getResultValue() : "",
                "resultType", result.getResultType() != null ? result.getResultType() : "",
                "executionTimeMs", result.getExecutionTimeMs(),
                "diagnostics", result.getDiagnostics(),
                "timestamp", java.time.LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            logger.error("Error executing code in JShell session: {}", jshellSessionId, e);
            sendErrorMessage(session, "Execution error: " + e.getMessage());
        }
    }

    private void handleCompletionRequest(WebSocketSession session, Map<String, Object> request) throws IOException {
        String partial = (String) request.get("partial");
        String jshellSessionId = sessionToJShellMapping.get(session.getId());
        
        if (jshellSessionId == null) {
            sendErrorMessage(session, "JShell session not found");
            return;
        }
        
        try {
            var completions = jshellService.getCompletions(jshellSessionId, partial);
            
            sendMessage(session, Map.of(
                "type", "completion_result",
                "completions", completions,
                "timestamp", java.time.LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            logger.error("Error getting completions for session: {}", jshellSessionId, e);
            sendErrorMessage(session, "Completion error: " + e.getMessage());
        }
    }

    private void handleHistoryRequest(WebSocketSession session, Map<String, Object> request) throws IOException {
        String jshellSessionId = sessionToJShellMapping.get(session.getId());
        
        if (jshellSessionId == null) {
            sendErrorMessage(session, "JShell session not found");
            return;
        }
        
        try {
            JShellSession jshellSession = jshellService.getSession(jshellSessionId);
            if (jshellSession != null) {
                sendMessage(session, Map.of(
                    "type", "history_result",
                    "history", jshellSession.getExecutionHistory(),
                    "timestamp", java.time.LocalDateTime.now()
                ));
            } else {
                sendErrorMessage(session, "JShell session not found");
            }
            
        } catch (Exception e) {
            logger.error("Error getting history for session: {}", jshellSessionId, e);
            sendErrorMessage(session, "History error: " + e.getMessage());
        }
    }

    private void sendMessage(WebSocketSession session, Map<String, Object> message) throws IOException {
        String json = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(json));
    }

    private void sendErrorMessage(WebSocketSession session, String error) throws IOException {
        sendMessage(session, Map.of(
            "type", "error",
            "message", error,
            "timestamp", java.time.LocalDateTime.now()
        ));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket transport error for session: {}", session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.info("WebSocket connection closed: {} with status: {}", session.getId(), closeStatus);
        
        // Clean up JShell session
        String jshellSessionId = sessionToJShellMapping.remove(session.getId());
        if (jshellSessionId != null) {
            jshellService.destroySession(jshellSessionId);
            logger.info("JShell session destroyed: {}", jshellSessionId);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
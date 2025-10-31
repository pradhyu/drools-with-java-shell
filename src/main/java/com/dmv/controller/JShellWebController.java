package com.dmv.controller;

import com.dmv.service.JShellService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Web controller for JShell interface
 */
@Controller
@RequestMapping("/jshell")
public class JShellWebController {

    private static final Logger logger = LoggerFactory.getLogger(JShellWebController.class);

    private final JShellService jshellService;

    @Autowired
    public JShellWebController(JShellService jshellService) {
        this.jshellService = jshellService;
    }

    /**
     * Serve the JShell web interface
     */
    @GetMapping
    public String jshellInterface(Model model) {
        logger.debug("Serving JShell web interface");
        
        model.addAttribute("title", "DMV Rules Engine - JShell REPL");
        model.addAttribute("timestamp", LocalDateTime.now());
        
        return "jshell";
    }

    /**
     * Get JShell service status
     */
    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getJShellStatus() {
        logger.debug("Getting JShell service status");
        
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("status", "ACTIVE");
            status.put("activeSessions", jshellService.getActiveSessions().size());
            status.put("sessionIds", jshellService.getActiveSessions());
            status.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            logger.error("Error getting JShell status", e);
            Map<String, Object> errorStatus = new HashMap<>();
            errorStatus.put("status", "ERROR");
            errorStatus.put("error", e.getMessage());
            errorStatus.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(500).body(errorStatus);
        }
    }
}
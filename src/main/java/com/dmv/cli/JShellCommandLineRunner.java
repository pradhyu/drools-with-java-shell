package com.dmv.cli;

import com.dmv.model.ExecutionResult;
import com.dmv.model.JShellSession;
import com.dmv.service.JShellService;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Command-line runner for JShell integration
 */
@Component
public class JShellCommandLineRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(JShellCommandLineRunner.class);
    private static final String JSHELL_PROMPT = "dmv-jshell> ";
    private static final String CONTINUATION_PROMPT = "         > ";

    private final JShellService jshellService;
    private final ApplicationContext applicationContext;

    @Autowired
    public JShellCommandLineRunner(JShellService jshellService, ApplicationContext applicationContext) {
        this.jshellService = jshellService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if --jshell flag is provided
        boolean jshellMode = false;
        for (String arg : args) {
            if ("--jshell".equals(arg)) {
                jshellMode = true;
                break;
            }
        }

        if (jshellMode) {
            startJShellMode();
        }
    }

    private void startJShellMode() {
        logger.info("Starting DMV Rules Engine in JShell mode");
        
        try {
            // Create terminal and line reader
            Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();
            
            LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

            // Create JShell session
            String sessionId = "cli-" + UUID.randomUUID().toString();
            JShellSession session = jshellService.createSession(sessionId);

            printWelcomeMessage();
            printHelpMessage();

            String line;
            StringBuilder multiLineInput = new StringBuilder();
            boolean inMultiLineMode = false;

            while (true) {
                try {
                    String prompt = inMultiLineMode ? CONTINUATION_PROMPT : JSHELL_PROMPT;
                    line = lineReader.readLine(prompt);

                    if (line == null) {
                        break; // EOF
                    }

                    line = line.trim();

                    // Handle special commands
                    if (handleSpecialCommands(line, session)) {
                        continue;
                    }

                    // Handle multi-line input
                    if (line.endsWith("\\")) {
                        // Continuation line
                        multiLineInput.append(line.substring(0, line.length() - 1)).append("\n");
                        inMultiLineMode = true;
                        continue;
                    } else if (inMultiLineMode) {
                        // End of multi-line input
                        multiLineInput.append(line);
                        line = multiLineInput.toString();
                        multiLineInput.setLength(0);
                        inMultiLineMode = false;
                    }

                    if (!line.isEmpty()) {
                        executeAndPrintResult(line, session);
                    }

                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                    logger.error("Error in JShell command line", e);
                }
            }

            // Cleanup
            jshellService.destroySession(sessionId);
            System.out.println("\nJShell session ended. Web application continues running...");

        } catch (IOException e) {
            logger.error("Failed to start JShell command line interface", e);
            System.err.println("Failed to start JShell: " + e.getMessage());
        }
    }

    private boolean handleSpecialCommands(String line, JShellSession session) {
        switch (line.toLowerCase()) {
            case "/exit":
            case "/quit":
                return false; // Will exit the loop
                
            case "/help":
                printHelpMessage();
                return true;
                
            case "/vars":
                executeAndPrintResult("/vars", session);
                return true;
                
            case "/methods":
                executeAndPrintResult("/methods", session);
                return true;
                
            case "/imports":
                executeAndPrintResult("/imports", session);
                return true;
                
            case "/history":
                printHistory(session);
                return true;
                
            case "/samples":
                printSampleCommands();
                return true;
                
            case "/clear":
                clearScreen();
                return true;
                
            default:
                return false; // Not a special command
        }
    }

    private void executeAndPrintResult(String code, JShellSession session) {
        try {
            ExecutionResult result = jshellService.executeCode(session.getSessionId(), code);
            
            if (result.isSuccess()) {
                if (result.getOutput() != null && !result.getOutput().trim().isEmpty()) {
                    System.out.println(result.getOutput().trim());
                }
                if (result.getResultValue() != null) {
                    System.out.println("=> " + result.getResultValue());
                }
            } else {
                if (result.getErrorOutput() != null && !result.getErrorOutput().trim().isEmpty()) {
                    System.err.println(result.getErrorOutput().trim());
                }
                if (result.hasDiagnostics()) {
                    for (String diagnostic : result.getDiagnostics()) {
                        System.err.println("Error: " + diagnostic);
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Execution error: " + e.getMessage());
            logger.error("Error executing code in JShell", e);
        }
    }

    private void printWelcomeMessage() {
        System.out.println("=".repeat(60));
        System.out.println("DMV Rules Engine - Interactive JShell Environment");
        System.out.println("=".repeat(60));
        System.out.println("Welcome to the DMV Rules Engine JShell interface!");
        System.out.println("You can execute Java code, test Drools rules, and interact with the system.");
        System.out.println();
    }

    private void printHelpMessage() {
        System.out.println("Available commands:");
        System.out.println("  /help      - Show this help message");
        System.out.println("  /exit      - Exit JShell (web application continues)");
        System.out.println("  /vars      - Show declared variables");
        System.out.println("  /methods   - Show declared methods");
        System.out.println("  /imports   - Show imported packages");
        System.out.println("  /history   - Show command history");
        System.out.println("  /samples   - Show sample commands");
        System.out.println("  /clear     - Clear screen");
        System.out.println();
        System.out.println("Pre-loaded variables:");
        System.out.println("  sampleAdult        - Valid adult renewal request");
        System.out.println("  sampleMinor        - Minor renewal request");
        System.out.println("  sampleExpired      - Expired license renewal");
        System.out.println("  sampleWithViolations - Request with violations");
        System.out.println();
    }

    private void printHistory(JShellSession session) {
        System.out.println("Command History:");
        int i = 1;
        for (String command : session.getExecutionHistory()) {
            System.out.printf("%3d: %s%n", i++, command);
        }
        System.out.println();
    }

    private void printSampleCommands() {
        System.out.println("Sample Commands:");
        System.out.println("  sampleAdult                    - Display sample adult request");
        System.out.println("  sampleAdult.getPersonalInfo()  - Get personal info");
        System.out.println("  sampleAdult.getCurrentLicense().isExpired()  - Check if expired");
        System.out.println();
        System.out.println("Rule Testing:");
        System.out.println("  // Create a decision object");
        System.out.println("  var decision = new RenewalDecision(DecisionType.APPROVED);");
        System.out.println("  // Test with rules (requires rules service integration)");
        System.out.println();
        System.out.println("JSON Conversion:");
        System.out.println("  factToJson(sampleAdult)        - Convert fact to JSON");
        System.out.println("  // jsonToFact(jsonString)      - Convert JSON to fact");
        System.out.println();
    }

    private void clearScreen() {
        // ANSI escape code to clear screen
        System.out.print("\033[2J\033[H");
        System.out.flush();
    }
}
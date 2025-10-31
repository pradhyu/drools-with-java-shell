package com.dmv.lsp;

import com.dmv.model.RuleCompilationResult;
import com.dmv.service.RulesManagementService;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DroolsTextDocumentService implements TextDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DroolsTextDocumentService.class);

    private final DroolsLanguageServer languageServer;
    private final RulesManagementService rulesManagementService;
    private final DroolsLanguageFeatures languageFeatures;

    public DroolsTextDocumentService(DroolsLanguageServer languageServer, RulesManagementService rulesManagementService) {
        this.languageServer = languageServer;
        this.rulesManagementService = rulesManagementService;
        this.languageFeatures = new DroolsLanguageFeatures();
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        TextDocumentItem document = params.getTextDocument();
        logger.info("Document opened: {}", document.getUri());
        
        languageServer.addDocument(document.getUri(), document);
        
        // Validate the document
        validateDocument(document.getUri(), document.getText());
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        logger.debug("Document changed: {}", uri);
        
        // Get the full text from the change event
        String newText = params.getContentChanges().get(0).getText();
        languageServer.updateDocument(uri, newText);
        
        // Validate the updated document
        validateDocument(uri, newText);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        logger.info("Document closed: {}", uri);
        
        languageServer.removeDocument(uri);
        
        // Clear diagnostics for closed document
        publishDiagnostics(uri, new ArrayList<>());
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        logger.info("Document saved: {}", uri);
        
        // Re-validate on save
        TextDocumentItem document = languageServer.getDocument(uri);
        if (document != null) {
            validateDocument(uri, document.getText());
        }
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        String uri = params.getTextDocument().getUri();
        Position position = params.getPosition();
        
        logger.debug("Hover request for {} at line {}, character {}", 
                    uri, position.getLine(), position.getCharacter());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                TextDocumentItem document = languageServer.getDocument(uri);
                if (document == null) {
                    return null;
                }
                
                Hover hover = languageFeatures.getEnhancedHover(document.getText(), position);
                if (hover != null) {
                    return hover;
                }
                
                return null;
                
            } catch (Exception e) {
                logger.error("Error providing hover information", e);
                return null;
            }
        });
    }

    // Completion - simplified for compatibility
    public CompletableFuture<List<CompletionItem>> getCompletions(CompletionParams params) {
        String uri = params.getTextDocument().getUri();
        Position position = params.getPosition();
        
        logger.debug("Completion request for {} at line {}, character {}", 
                    uri, position.getLine(), position.getCharacter());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                TextDocumentItem document = languageServer.getDocument(uri);
                if (document == null) {
                    return new ArrayList<>();
                }
                
                return languageFeatures.getEnhancedCompletions(document.getText(), position);
                
            } catch (Exception e) {
                logger.error("Error providing completion", e);
                return new ArrayList<>();
            }
        });
    }

    private void validateDocument(String uri, String content) {
        logger.debug("Validating document: {}", uri);
        
        try {
            RuleCompilationResult result = rulesManagementService.compileRule(content);
            List<Diagnostic> diagnostics = languageFeatures.getEnhancedDiagnostics(
                content, result.getErrors(), result.getWarnings());
            
            publishDiagnostics(uri, diagnostics);
            
        } catch (Exception e) {
            logger.error("Error validating document: {}", uri, e);
            
            // Create error diagnostic
            List<Diagnostic> diagnostics = new ArrayList<>();
            Diagnostic diagnostic = new Diagnostic();
            diagnostic.setRange(new Range(new Position(0, 0), new Position(0, 0)));
            diagnostic.setSeverity(DiagnosticSeverity.Error);
            diagnostic.setMessage("Validation error: " + e.getMessage());
            diagnostic.setSource("drools-lsp");
            diagnostics.add(diagnostic);
            
            publishDiagnostics(uri, diagnostics);
        }
    }

    private void publishDiagnostics(String uri, List<Diagnostic> diagnostics) {
        if (languageServer.getClient() != null) {
            PublishDiagnosticsParams params = new PublishDiagnosticsParams();
            params.setUri(uri);
            params.setDiagnostics(diagnostics);
            
            languageServer.getClient().publishDiagnostics(params);
            logger.debug("Published {} diagnostics for {}", diagnostics.size(), uri);
        }
    }


}
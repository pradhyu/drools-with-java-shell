package com.dmv.lsp;

import com.dmv.service.RulesManagementService;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DroolsLanguageServer implements LanguageServer {

    private static final Logger logger = LoggerFactory.getLogger(DroolsLanguageServer.class);

    private final RulesManagementService rulesManagementService;
    private final DroolsTextDocumentService textDocumentService;
    private final DroolsWorkspaceService workspaceService;
    private LanguageClient client;
    private final ConcurrentHashMap<String, TextDocumentItem> openDocuments = new ConcurrentHashMap<>();

    @Autowired
    public DroolsLanguageServer(RulesManagementService rulesManagementService) {
        this.rulesManagementService = rulesManagementService;
        this.textDocumentService = new DroolsTextDocumentService(this, rulesManagementService);
        this.workspaceService = new DroolsWorkspaceService(this);
        logger.info("Drools Language Server initialized");
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        logger.info("LSP Initialize request received from client: {}", params.getClientInfo());

        ServerCapabilities capabilities = new ServerCapabilities();
        
        // Text document sync
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
        
        // Completion support
        CompletionOptions completionOptions = new CompletionOptions();
        completionOptions.setResolveProvider(false);
        completionOptions.setTriggerCharacters(List.of(".", " ", "(", ","));
        capabilities.setCompletionProvider(completionOptions);
        
        // Hover support
        capabilities.setHoverProvider(true);
        
        // Diagnostic support (validation)
        capabilities.setDiagnosticProvider(new DiagnosticRegistrationOptions());
        
        // Document formatting
        capabilities.setDocumentFormattingProvider(false);
        
        // Code action support
        capabilities.setCodeActionProvider(true);

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setName("Drools Language Server");
        serverInfo.setVersion("1.0.0");

        InitializeResult result = new InitializeResult(capabilities, serverInfo);
        
        logger.info("LSP Initialize completed with capabilities: completion, hover, diagnostics");
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public void initialized(InitializedParams params) {
        logger.info("LSP Client initialized");
        
        // Register for configuration changes if needed
        if (client != null) {
            // Could register for workspace/configuration requests here
        }
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        logger.info("LSP Shutdown request received");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() {
        logger.info("LSP Exit request received");
        System.exit(0);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    // Connect method - simplified for compatibility
    public void connectClient(LanguageClient client) {
        this.client = client;
        logger.info("LSP Client connected");
    }

    // Helper methods for document management
    public void addDocument(String uri, TextDocumentItem document) {
        openDocuments.put(uri, document);
        logger.debug("Document added: {}", uri);
    }

    public void updateDocument(String uri, String newText) {
        TextDocumentItem document = openDocuments.get(uri);
        if (document != null) {
            // Create updated document
            TextDocumentItem updatedDocument = new TextDocumentItem(
                document.getUri(),
                document.getLanguageId(),
                document.getVersion() + 1,
                newText
            );
            openDocuments.put(uri, updatedDocument);
            logger.debug("Document updated: {}", uri);
        }
    }

    public void removeDocument(String uri) {
        openDocuments.remove(uri);
        logger.debug("Document removed: {}", uri);
    }

    public TextDocumentItem getDocument(String uri) {
        return openDocuments.get(uri);
    }

    public LanguageClient getClient() {
        return client;
    }

    public List<String> getOpenDocumentUris() {
        return new ArrayList<>(openDocuments.keySet());
    }
}
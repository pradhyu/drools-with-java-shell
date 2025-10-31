package com.dmv.lsp;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class DroolsLSPServerLauncher implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DroolsLSPServerLauncher.class);
    private static final int LSP_PORT = 8081;

    private final DroolsLanguageServer languageServer;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    public DroolsLSPServerLauncher(DroolsLanguageServer languageServer) {
        this.languageServer = languageServer;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if --lsp flag is provided
        boolean lspMode = false;
        for (String arg : args) {
            if ("--lsp".equals(arg)) {
                lspMode = true;
                break;
            }
        }

        if (lspMode) {
            startLSPServer();
        } else {
            // Start LSP server in background for IDE integration
            executorService.submit(this::startLSPServerBackground);
        }
    }

    private void startLSPServer() {
        logger.info("Starting Drools LSP Server on port {}", LSP_PORT);
        
        try (ServerSocket serverSocket = new ServerSocket(LSP_PORT)) {
            logger.info("Drools LSP Server listening on port {}", LSP_PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("LSP Client connected from: {}", clientSocket.getRemoteSocketAddress());
                
                // Handle each client connection in a separate thread
                executorService.submit(() -> handleClientConnection(clientSocket));
            }
            
        } catch (Exception e) {
            logger.error("Error starting LSP server", e);
        }
    }

    private void startLSPServerBackground() {
        try {
            startLSPServer();
        } catch (Exception e) {
            logger.error("Error in background LSP server", e);
        }
    }

    private void handleClientConnection(Socket clientSocket) {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            
            // Create LSP launcher
            Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(
                languageServer, inputStream, outputStream);
            
            // Connect the language server to the client
            LanguageClient client = launcher.getRemoteProxy();
            languageServer.connectClient(client);
            
            logger.info("LSP Server connected to client");
            
            // Start listening for client messages
            launcher.startListening().get();
            
        } catch (Exception e) {
            logger.error("Error handling LSP client connection", e);
        } finally {
            try {
                clientSocket.close();
                logger.info("LSP Client connection closed");
            } catch (Exception e) {
                logger.error("Error closing client socket", e);
            }
        }
    }
}
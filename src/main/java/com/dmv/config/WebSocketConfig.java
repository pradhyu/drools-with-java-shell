package com.dmv.config;

import com.dmv.websocket.JShellWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final JShellWebSocketHandler jshellWebSocketHandler;

    @Autowired
    public WebSocketConfig(JShellWebSocketHandler jshellWebSocketHandler) {
        this.jshellWebSocketHandler = jshellWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(jshellWebSocketHandler, "/ws/jshell")
                .setAllowedOrigins("*"); // Configure appropriately for production
    }
}
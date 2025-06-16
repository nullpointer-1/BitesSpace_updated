package com.example.shopmanagement.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Application destination prefixes: for messages from clients to the server
        config.setApplicationDestinationPrefixes("/app");
        // Enable a simple in-memory broker
        config.enableSimpleBroker("/topic", "/queue");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the "/ws" endpoint which clients can connect to via SockJS
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:8081", "http://127.0.0.1:8081") // Ensure frontend origin is allowed
                .withSockJS(); // Enable SockJS fallback options
    }
}
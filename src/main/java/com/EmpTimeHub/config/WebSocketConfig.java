package com.EmpTimeHub.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for setting up WebSocket messaging with STOMP protocol.
 * Enables a simple in-memory message broker for broadcasting and configures
 * the application destination prefix for client-to-server messaging.
 * Registers the WebSocket endpoint with SockJS fallback support.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker for handling messages.
     * Enables a simple broker for topics and sets the application destination prefix.
     *
     * @param config the message broker registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // For broadcasting
        config.setApplicationDestinationPrefixes("/app"); // For client-to-server messages
    }

    /**
     * Registers STOMP endpoints for WebSocket connections.
     * Adds the "/ws" endpoint with SockJS fallback and allows all origins.
     *
     * @param registry the STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS(); // WebSocket endpoint
        //  Native WebSocket endpoint for React Native
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }
}


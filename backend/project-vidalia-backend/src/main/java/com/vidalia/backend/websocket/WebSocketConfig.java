package com.vidalia.backend.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketProperties webSocketProperties;
    private final WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;
    private final WebSocketPrincipalHandshakeHandler webSocketPrincipalHandshakeHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(webSocketProperties.getEndpoint())
                .addInterceptors(webSocketHandshakeInterceptor)
                .setHandshakeHandler(webSocketPrincipalHandshakeHandler)
                .setAllowedOriginPatterns(webSocketProperties.getAllowedOriginPatterns());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(webSocketProperties.getApplicationDestinationPrefix());
        registry.enableSimpleBroker(webSocketProperties.getBrokerDestinationPrefixes());
        registry.setUserDestinationPrefix(webSocketProperties.getUserDestinationPrefix());
    }
}



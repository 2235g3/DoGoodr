package com.vidalia.backend.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.jspecify.annotations.NonNull;

import java.security.Principal;
import java.util.Map;

@Component
public class WebSocketPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(@NonNull ServerHttpRequest request, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        String userId = (String) attributes.get(WebSocketHandshakeInterceptor.getUserIdAttributeName());
        if (userId == null || userId.isBlank()) {
            return null;
        }

        return () -> userId;
    }
}




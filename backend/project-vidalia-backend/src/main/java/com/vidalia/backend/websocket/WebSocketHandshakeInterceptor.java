package com.vidalia.backend.websocket;

import com.vidalia.backend.model.User;
import com.vidalia.backend.repository.UserRepository;
import com.vidalia.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private static final String USER_ID_ATTRIBUTE = "wsUserId";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = com.vidalia.backend.security.JwtTokenUtil.extractToken(request);
        if (token == null || token.isBlank()) {
            return false;
        }

        String email = jwtService.extractEmail(token);
        User user = email == null ? null : userRepository.findUserByEmail(email).orElse(null);
        if (user == null || !jwtService.isAccessTokenValid(token, user)) {
            return false;
        }

        attributes.put(USER_ID_ATTRIBUTE, user.getId().toString());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }


    static String getUserIdAttributeName() {
        return USER_ID_ATTRIBUTE;
    }
}



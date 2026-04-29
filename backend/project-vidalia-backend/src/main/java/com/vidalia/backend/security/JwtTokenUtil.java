package com.vidalia.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.util.UriComponentsBuilder;

public final class JwtTokenUtil {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_QUERY_PARAM = "token";
    private static final String ACCESS_TOKEN_QUERY_PARAM = "access_token";

    private JwtTokenUtil() { }

    public static String extractToken(HttpServletRequest req) {
        if (req == null) return null;
        String header = req.getHeader(AUTHORIZATION_HEADER);
        if (hasText(header)) {
            return stripBearer(header);
        }

        String token = req.getParameter(TOKEN_QUERY_PARAM);
        if (!hasText(token)) token = req.getParameter(ACCESS_TOKEN_QUERY_PARAM);
        return blankToNull(token);
    }

    public static String extractToken(ServerHttpRequest request) {
        if (request == null) return null;
        String header = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (hasText(header)) {
            return stripBearer(header);
        }

        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst(TOKEN_QUERY_PARAM);
        if (!hasText(token)) {
            token = UriComponentsBuilder.fromUri(request.getURI())
                    .build()
                    .getQueryParams()
                    .getFirst(ACCESS_TOKEN_QUERY_PARAM);
        }
        return blankToNull(token);
    }

    public static String extractToken(StompHeaderAccessor accessor) {
        if (accessor == null) return null;
        String header = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
        if (hasText(header)) {
            return stripBearer(header);
        }
        String token = accessor.getFirstNativeHeader(TOKEN_QUERY_PARAM);
        if (!hasText(token)) token = accessor.getFirstNativeHeader(ACCESS_TOKEN_QUERY_PARAM);
        return blankToNull(token);
    }

    private static String stripBearer(String header) {
        return header.startsWith(BEARER_PREFIX) ? header.substring(BEARER_PREFIX.length()).trim() : header.trim();
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String blankToNull(String s) {
        return hasText(s) ? s.trim() : null;
    }
}


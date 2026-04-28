package com.vidalia.backend.websocket;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.websocket")
@Getter
@Setter
public class WebSocketProperties {

    private String endpoint = "/ws";
    private String applicationDestinationPrefix = "/app";
    private String[] brokerDestinationPrefixes = new String[]{"/topic", "/queue"};
    private String userDestinationPrefix = "/user";
    private String[] allowedOriginPatterns = new String[]{"*"};
}



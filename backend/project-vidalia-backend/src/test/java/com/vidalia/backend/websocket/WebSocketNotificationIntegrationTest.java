package com.vidalia.backend.websocket;

import com.vidalia.backend.dto.notification.CreateNotificationDTO;
import com.vidalia.backend.dto.notification.NotificationResponseDTO;
import com.vidalia.backend.model.NotificationType;
import com.vidalia.backend.model.Role;
import com.vidalia.backend.model.User;
import com.vidalia.backend.repository.UserRepository;
import com.vidalia.backend.security.JwtService;
import com.vidalia.backend.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Type;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class WebSocketNotificationIntegrationTest {

    private static final String NOTIFICATION_SUBSCRIPTION_DESTINATION = "/user/queue/notifications";

    @LocalServerPort
    private int port;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Test
    @Timeout(20)
    void subscribedUserReceivesNotificationWhenBackendPublishesIt() throws Exception {
        User user = new User();
        user.setEmail("ws-user@example.test");
        user.setPassword("Password123!");
        user.setRole(Role.VOLUNTEER);
        user = userRepository.save(user);

        String token = jwtService.generateAccessToken(user);

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());

        BlockingQueue<NotificationResponseDTO> receivedNotifications = new ArrayBlockingQueue<>(1);

        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
        handshakeHeaders.add("Authorization", "Bearer " + token);

        StompSession session = stompClient
                .connectAsync(
                        "ws://localhost:" + port + "/ws",
                        handshakeHeaders,
                        new StompHeaders(),
                        new StompSessionHandlerAdapter() { })
                .get(10, TimeUnit.SECONDS);

        try {
            session.subscribe(NOTIFICATION_SUBSCRIPTION_DESTINATION, new StompFrameHandler() {
                @Override
                public @NonNull Type getPayloadType(@NonNull StompHeaders headers) {
                    return NotificationResponseDTO.class;
                }

                @Override
                public void handleFrame(@NonNull StompHeaders headers, Object payload) {
                    try {
                        receivedNotifications.put((NotificationResponseDTO) payload);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Interrupted while waiting for websocket notification", interruptedException);
                    }
                }
            });

            CreateNotificationDTO createDto = new CreateNotificationDTO();
            createDto.setRecipientId(user.getId());
            createDto.setType(NotificationType.APPLICATION_RECEIVED);
            createDto.setMessage("Real websocket notification test");

            NotificationResponseDTO created = notificationService.createNotification(createDto);
            NotificationResponseDTO received = receivedNotifications.poll(10, TimeUnit.SECONDS);

            assertNotNull(received, "Expected a websocket notification to be delivered to the subscribed user");
            assertEquals(created.getId(), received.getId());
            assertEquals(created.getType(), received.getType());
            assertEquals(created.getMessage(), received.getMessage());
            assertEquals(created.isRead(), received.isRead());
        } finally {
            session.disconnect();
            stompClient.stop();
        }
    }

    @Test
    @Timeout(20)
    void websocketConnectionIsRejectedWithoutJwtToken() {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());

        try {
            assertThrows(ExecutionException.class, () -> stompClient
                    .connectAsync(
                            "ws://localhost:" + port + "/ws",
                            new WebSocketHttpHeaders(),
                            new StompHeaders(),
                            new StompSessionHandlerAdapter() { })
                    .get(10, TimeUnit.SECONDS));
        } finally {
            stompClient.stop();
        }
    }
}

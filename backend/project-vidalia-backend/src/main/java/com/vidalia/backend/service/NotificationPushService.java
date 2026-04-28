package com.vidalia.backend.service;

import com.vidalia.backend.dto.notification.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationPushService {

    private static final String NOTIFICATION_QUEUE_DESTINATION = "/queue/notifications";

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(UUID userId, NotificationResponseDTO notification) {
        messagingTemplate.convertAndSendToUser(userId.toString(), NOTIFICATION_QUEUE_DESTINATION, notification);
    }
}


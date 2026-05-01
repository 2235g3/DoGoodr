package com.vidalia.backend.dto.notification;

import com.vidalia.backend.model.NotificationType;
import lombok.Data;

import java.util.UUID;

@Data
public class NotificationResponseDTO {
    private UUID id;
    private NotificationType type;
    private String message;
    private String timestamp;
    private boolean read;
}

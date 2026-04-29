package com.vidalia.backend.dto.notification;

import com.vidalia.backend.model.NotificationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateNotificationDTO {
    @NotNull(message = "User id cannot be null")
    private UUID userId;

    @NotNull(message = "Notification type cannot be null")
    private NotificationType type;

    @NotNull(message = "Message cannot be null")
    @Size(max = 2000, message = "Message cannot exceed 2000 characters")
    private String message;
}


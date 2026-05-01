package com.vidalia.backend.controller.notification;

import com.vidalia.backend.dto.notification.CreateNotificationDTO;
import com.vidalia.backend.dto.notification.NotificationResponseDTO;
import com.vidalia.backend.security.CustomUserDetails;
import com.vidalia.backend.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER', 'ORGANISATION')")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(notificationService.getAllNotificationsForUser(userId));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(notificationService.getAllUnreadNotificationsForUser(userId));
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID notificationId) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(notificationService.getNotificationById(userId, notificationId));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID notificationId) {
        UUID userId = userDetails.getId();
        notificationService.markNotificationAsRead(userId, notificationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @Valid @RequestBody CreateNotificationDTO createNotificationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.createNotification(createNotificationDTO));
    }
}


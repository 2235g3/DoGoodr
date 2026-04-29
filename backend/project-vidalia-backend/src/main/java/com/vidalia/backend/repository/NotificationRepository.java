package com.vidalia.backend.repository;

import com.vidalia.backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByUserIdOrderByTimestampDesc(UUID userId);
    List<Notification> findAllByUserIdAndReadFalseOrderByTimestampDesc(UUID userId);
    Optional<Notification> findByIdAndUserId(UUID notificationId, UUID userId);
}

package com.vidalia.backend.service;

import com.vidalia.backend.dto.notification.CreateNotificationDTO;
import com.vidalia.backend.dto.notification.NotificationResponseDTO;
import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.mapper.NotificationMapper;
import com.vidalia.backend.model.Notification;
import com.vidalia.backend.model.User;
import com.vidalia.backend.repository.NotificationRepository;
import com.vidalia.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private static final String NOTIFICATION_QUEUE_DESTINATION = "/queue/notifications";
    private final SimpMessagingTemplate messagingTemplate;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getAllNotificationsForUser(UUID userId) {
        return notificationRepository.findAllByUserIdOrderByTimestampDesc(userId).stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getAllUnreadNotificationsForUser(UUID userId) {
        return notificationRepository.findAllByUserIdAndReadFalseOrderByTimestampDesc(userId).stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificationResponseDTO getNotificationById(UUID userId, UUID notificationId) {
        return notificationRepository.findByIdAndUserId(notificationId, userId)
                .map(notificationMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
    }

    @Transactional
    public void markNotificationAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public NotificationResponseDTO createNotification(CreateNotificationDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        Notification notification = notificationMapper.toEntity(dto, user);
        Notification savedNotification = notificationRepository.save(notification);
        NotificationResponseDTO response = notificationMapper.toDTO(savedNotification);
        pushToUser(user.getId(), response);
        return response;

    }

    private void pushToUser(UUID userId, NotificationResponseDTO notification) {
        messagingTemplate.convertAndSendToUser(userId.toString(), NOTIFICATION_QUEUE_DESTINATION, notification);
    }
}

package com.vidalia.backend.service;

import com.vidalia.backend.dto.notification.CreateNotificationDTO;
import com.vidalia.backend.dto.notification.NotificationResponseDTO;
import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.mapper.NotificationMapper;
import com.vidalia.backend.model.Notification;
import com.vidalia.backend.model.NotificationType;
import com.vidalia.backend.model.Role;
import com.vidalia.backend.model.User;
import com.vidalia.backend.repository.NotificationRepository;
import com.vidalia.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private NotificationMapper notificationMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void createNotification_persistsAndPushesRealtimeMessage() {
        UUID userId = UUID.randomUUID();

        CreateNotificationDTO createDto = new CreateNotificationDTO();
        createDto.setUserId(userId);
        createDto.setType(NotificationType.APPLICATION_RECEIVED);
        createDto.setMessage("New application received");

        User user = new User();
        user.setId(userId);
        user.setEmail("service-user@example.test");
        user.setPassword("Password123!");
        user.setRole(Role.VOLUNTEER);

        Notification toSave = new Notification();
        Notification saved = new Notification();
        saved.setId(UUID.randomUUID());
        saved.setUser(user);
        saved.setType(NotificationType.APPLICATION_RECEIVED);
        saved.setMessage("New application received");

        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setId(saved.getId());
        response.setType(saved.getType());
        response.setMessage(saved.getMessage());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationMapper.toEntity(createDto, user)).thenReturn(toSave);
        when(notificationRepository.save(toSave)).thenReturn(saved);
        when(notificationMapper.toDTO(saved)).thenReturn(response);

        NotificationResponseDTO actual = notificationService.createNotification(createDto);

        assertSame(response, actual);
        verify(notificationRepository).save(toSave);
        verify(messagingTemplate).convertAndSendToUser(userId.toString(), "/queue/notifications", response);
    }

    @Test
    void getNotificationById_throwsWhenNotificationDoesNotBelongToUser() {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();

        when(notificationRepository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.getNotificationById(userId, notificationId));
    }

    @Test
    void markNotificationAsRead_throwsWhenNotificationDoesNotBelongToUser() {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();

        when(notificationRepository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.markNotificationAsRead(userId, notificationId));
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}


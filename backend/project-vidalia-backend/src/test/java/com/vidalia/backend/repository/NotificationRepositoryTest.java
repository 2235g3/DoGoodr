package com.vidalia.backend.repository;

import com.vidalia.backend.model.Notification;
import com.vidalia.backend.model.NotificationType;
import com.vidalia.backend.model.Role;
import com.vidalia.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByUserIdOrderByTimestampDesc_returnsNewestFirst() {
        User user = saveUser("repo-order@example.test");

        Notification oldN = saveNotification(user, "old", LocalDateTime.now().minusHours(2), false);
        Notification newestN = saveNotification(user, "newest", LocalDateTime.now().minusMinutes(10), false);
        Notification middleN = saveNotification(user, "middle", LocalDateTime.now().minusHours(1), true);

        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByTimestampDesc(user.getId());

        assertEquals(3, notifications.size());
        assertEquals(newestN.getId(), notifications.get(0).getId());
        assertEquals(middleN.getId(), notifications.get(1).getId());
        assertEquals(oldN.getId(), notifications.get(2).getId());
    }

    @Test
    void findAllByUserIdAndReadFalseOrderByTimestampDesc_returnsUnreadNewestFirst() {
        User user = saveUser("repo-unread@example.test");

        Notification olderUnread = saveNotification(user, "older unread", LocalDateTime.now().minusHours(3), false);
        saveNotification(user, "read", LocalDateTime.now().minusHours(2), true);
        Notification newestUnread = saveNotification(user, "newer unread", LocalDateTime.now().minusMinutes(5), false);

        List<Notification> unreadNotifications = notificationRepository.findAllByUserIdAndReadFalseOrderByTimestampDesc(user.getId());

        assertEquals(2, unreadNotifications.size());
        assertEquals(newestUnread.getId(), unreadNotifications.get(0).getId());
        assertEquals(olderUnread.getId(), unreadNotifications.get(1).getId());
        assertFalse(unreadNotifications.get(0).isRead());
        assertFalse(unreadNotifications.get(1).isRead());
    }

    @Test
    void findByIdAndUserId_enforcesOwnership() {
        User owner = saveUser("repo-owner@example.test");
        User otherUser = saveUser("repo-other@example.test");
        Notification notification = saveNotification(owner, "owner only", LocalDateTime.now(), false);

        assertTrue(notificationRepository.findByIdAndUserId(notification.getId(), owner.getId()).isPresent());
        assertTrue(notificationRepository.findByIdAndUserId(notification.getId(), otherUser.getId()).isEmpty());
    }

    private User saveUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("Password123!");
        user.setRole(Role.VOLUNTEER);
        return userRepository.save(user);
    }

    private Notification saveNotification(User user, String message, LocalDateTime timestamp, boolean isRead) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(NotificationType.APPLICATION_RECEIVED);
        notification.setMessage(message);
        notification.setTimestamp(timestamp);
        notification.setRead(isRead);
        return notificationRepository.save(notification);
    }
}



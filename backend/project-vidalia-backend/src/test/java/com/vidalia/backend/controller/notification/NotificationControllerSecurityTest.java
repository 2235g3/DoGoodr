package com.vidalia.backend.controller.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vidalia.backend.dto.notification.CreateNotificationDTO;
import com.vidalia.backend.dto.notification.NotificationResponseDTO;
import com.vidalia.backend.model.NotificationType;
import com.vidalia.backend.model.Role;
import com.vidalia.backend.model.User;
import com.vidalia.backend.security.CustomUserDetails;
import com.vidalia.backend.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void getNotifications_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getNotifications_allowsAuthenticatedRole() throws Exception {
        CustomUserDetails volunteer = customUserDetails(Role.VOLUNTEER);
        when(notificationService.getAllNotificationsForUser(volunteer.getId())).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications").with(user(volunteer)))
                .andExpect(status().isOk());
    }

    @Test
    void createNotification_forbiddenForNonAdmin() throws Exception {
        CustomUserDetails volunteer = customUserDetails(Role.VOLUNTEER);

        CreateNotificationDTO createDto = new CreateNotificationDTO();
        createDto.setUserId(UUID.randomUUID());
        createDto.setType(NotificationType.APPLICATION_RECEIVED);
        createDto.setMessage("Test notification");

        mockMvc.perform(post("/api/notifications")
                        .with(user(volunteer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createNotification_allowsAdmin() throws Exception {
        CustomUserDetails admin = customUserDetails(Role.ADMIN);

        CreateNotificationDTO createDto = new CreateNotificationDTO();
        createDto.setUserId(UUID.randomUUID());
        createDto.setType(NotificationType.APPLICATION_RECEIVED);
        createDto.setMessage("Test notification");

        NotificationResponseDTO responseDto = new NotificationResponseDTO();
        responseDto.setId(UUID.randomUUID());
        responseDto.setType(NotificationType.APPLICATION_RECEIVED);
        responseDto.setMessage("Test notification");

        when(notificationService.createNotification(any(CreateNotificationDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/notifications")
                        .with(user(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());
    }

    private CustomUserDetails customUserDetails(Role role) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(role.name().toLowerCase() + "@example.test");
        user.setPassword("Password123!");
        user.setRole(role);
        return new CustomUserDetails(user);
    }
}





package com.vidalia.backend.mapper;

import com.vidalia.backend.dto.notification.CreateNotificationDTO;
import com.vidalia.backend.dto.notification.NotificationResponseDTO;
import com.vidalia.backend.model.Notification;
import com.vidalia.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

	public Notification toEntity(CreateNotificationDTO dto, User user) {
		Notification notification = new Notification();
		notification.setUser(user);
		notification.setType(dto.getType());
		notification.setMessage(dto.getMessage());
		return notification;
	}

	public NotificationResponseDTO toDTO(Notification notification) {
		NotificationResponseDTO dto = new NotificationResponseDTO();
		dto.setId(notification.getId());
		dto.setType(notification.getType());
		dto.setMessage(notification.getMessage());
		dto.setTimestamp(notification.getTimestamp() != null ? notification.getTimestamp().toString() : null);
		dto.setRead(notification.isRead());
		return dto;
	}
}

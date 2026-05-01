package com.vidalia.backend.dto.user;

import com.vidalia.backend.model.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID id;
    private String email;
    private String secondaryEmail;
    private String phoneNumber;
    private Role role;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

}

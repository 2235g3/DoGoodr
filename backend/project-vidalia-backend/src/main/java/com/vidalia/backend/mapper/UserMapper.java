package com.vidalia.backend.mapper;

import com.vidalia.backend.dto.auth.RegisterRequest;
import com.vidalia.backend.dto.user.CreateUserDTO;
import com.vidalia.backend.dto.user.UpdateUserDTO;
import com.vidalia.backend.dto.user.UserResponseDTO;
import com.vidalia.backend.model.Role;
import com.vidalia.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(CreateUserDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        return user;
    }

    public UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setSecondaryEmail(user.getSecondaryEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setLastLogin(user.getLastLogin());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public void updateUser(User user, UpdateUserDTO dto) {
        if  (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getSecondaryEmail() != null) {
            user.setSecondaryEmail(dto.getSecondaryEmail());
        }
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
    }

    public CreateUserDTO fromRegisterRequest(RegisterRequest request, Role role) {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setEmail(request.getEmail());
        dto.setPassword(request.getPassword());
        dto.setRole(role);
        return dto;
    }


}

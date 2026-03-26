package com.vidalia.backend.service;

import com.vidalia.backend.dto.user.CreateUserDTO;
import com.vidalia.backend.dto.user.UpdateUserDTO;
import com.vidalia.backend.dto.user.UpdateUserPasswordDTO;
import com.vidalia.backend.dto.user.UserResponseDTO;
import com.vidalia.backend.exceptions.IncorrectOldPasswordException;
import com.vidalia.backend.exceptions.ResourceAlreadyExistsException;
import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.mapper.UserMapper;
import com.vidalia.backend.model.Role;
import com.vidalia.backend.model.User;
import com.vidalia.backend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all users with a specified role (VOLUNTEER, ORGANISATION, ADMIN)
     * @param role the role to filter users by
     * @return List of users with role
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByRole(Role role) {
        return userRepository.findAllByRole(role).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {
        //Check that the email is unique/ not already registered
        if (userRepository.existsByEmailIgnoreCase(createUserDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        User user = userMapper.toEntity(createUserDTO);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);

        //Hash password before saving entity
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }
        return userMapper.toDTO(user);

    }

    /**
     * Updates all user fields except password, which has its own service method
     * @param id Id of the user to update
     * @param dto Details to update
     * @return Updated user details
     */
    @Transactional
    public UserResponseDTO updateUser(UUID id, @Valid UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        //Check email uniqueness only if it's being updated
        boolean isEmailUpdate = dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(user.getEmail());

        if (isEmailUpdate && userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        //Check secondary email uniqueness only if it's being updated
        boolean isSecondEmailUpdate = dto.getSecondaryEmail() != null
                && !dto.getSecondaryEmail().equalsIgnoreCase(user.getSecondaryEmail());

        if (isSecondEmailUpdate && userRepository.existsBySecondaryEmailIgnoreCase(dto.getSecondaryEmail())) {
            throw new ResourceAlreadyExistsException("Secondary email already exists");
        }

        //Check phone number uniqueness only if it's being updated
        boolean isPhoneUpdate = dto.getPhoneNumber() != null
                && !dto.getPhoneNumber().equals(user.getPhoneNumber());
        if (isPhoneUpdate && userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ResourceAlreadyExistsException("Phone number already exists");
        }

        userMapper.updateUser(user, dto);
        userRepository.save(user);
        return userMapper.toDTO(user);

    }

    @Transactional
    public void updateUserPassword(UUID id, @Valid UpdateUserPasswordDTO dto) {
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        //Check that user's old password matches records
        if (dto.getOldPassword() == null || !passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IncorrectOldPasswordException();
        }

        //Hash new password before saving
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

}

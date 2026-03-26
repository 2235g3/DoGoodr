package com.vidalia.backend.service;

import com.vidalia.backend.dto.auth.AuthResponse;
import com.vidalia.backend.dto.auth.LoginRequest;
import com.vidalia.backend.dto.auth.RegisterRequest;
import com.vidalia.backend.dto.user.CreateUserDTO;
import com.vidalia.backend.dto.user.UserResponseDTO;
import com.vidalia.backend.mapper.UserMapper;
import com.vidalia.backend.model.Role;
import com.vidalia.backend.model.User;
import com.vidalia.backend.repository.UserRepository;
import com.vidalia.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );
        User user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

        setLastLogin(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse registerUser(RegisterRequest request, Role role) {

        CreateUserDTO  createUserDTO = userMapper.fromRegisterRequest(request, role);
        UserResponseDTO savedUserDTO = userService.createUser(createUserDTO);

        User savedUser = userRepository.findUserByEmail(savedUserDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after creation"));

        setLastLogin(savedUser);

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    protected void setLastLogin(User user) {
        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);
    }



}

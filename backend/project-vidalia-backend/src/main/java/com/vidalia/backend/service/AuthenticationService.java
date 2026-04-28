package com.vidalia.backend.service;

import com.vidalia.backend.dto.auth.AuthResponse;
import com.vidalia.backend.dto.auth.LoginRequest;
import com.vidalia.backend.dto.auth.RefreshTokenRequest;
import com.vidalia.backend.dto.auth.ORegisterRequest;
import com.vidalia.backend.dto.auth.VRegisterRequest;
import com.vidalia.backend.dto.profile.CreateOrganisationProfileDTO;
import com.vidalia.backend.dto.profile.CreateVolunteerProfileDTO;
import com.vidalia.backend.dto.user.CreateUserDTO;
import com.vidalia.backend.dto.user.UserResponseDTO;
import com.vidalia.backend.mapper.UserMapper;
import com.vidalia.backend.model.User;
import com.vidalia.backend.repository.UserRepository;
import com.vidalia.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final OrganisationProfileService organisationProfileService;
    private final VolunteerProfileService volunteerProfileService;
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
                .orElseThrow(() -> new AuthenticationServiceException("Authenticated user could not be loaded"));

        return issueTokensForUser(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String email;
        try {
            email = jwtService.extractEmail(refreshToken);
        } catch (RuntimeException exception) {
            throw new BadCredentialsException("Invalid refresh token", exception);
        }

        if (email == null || email.isBlank()) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (!jwtService.isRefreshTokenValid(refreshToken, user)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public AuthResponse registerVolunteer(VRegisterRequest request) {
        CreateUserDTO createUserDTO = userMapper.fromVolunteerRegisterRequest(request);
        User savedUser = createAndLoadUser(createUserDTO);

        CreateVolunteerProfileDTO profileDTO = new CreateVolunteerProfileDTO();
        profileDTO.setForename(request.getForename());
        profileDTO.setSurname(request.getSurname());
        profileDTO.setPreferredName(request.getPreferedName());
        profileDTO.setDateOfBirth(request.getDateOfBirth());

        volunteerProfileService.createVolunteerProfile(profileDTO, savedUser.getId());
        return issueTokensForUser(savedUser);
    }

    @Transactional
    public AuthResponse registerOrganisation(ORegisterRequest request) {
        CreateUserDTO createUserDTO = userMapper.fromOrganisationRegisterRequest(request);
        User savedUser = createAndLoadUser(createUserDTO);

        CreateOrganisationProfileDTO profileDTO = new CreateOrganisationProfileDTO();
        profileDTO.setDisplayName(request.getDisplayName());
        profileDTO.setAccountType(request.getAccountType());

        organisationProfileService.createOrganisationProfile(profileDTO, savedUser.getId());
        return issueTokensForUser(savedUser);
    }

    private User createAndLoadUser(CreateUserDTO createUserDTO) {
        UserResponseDTO savedUserDTO = userService.createUser(createUserDTO);
        return userRepository.findById(savedUserDTO.getId())
                .orElseThrow(() -> new AuthenticationServiceException("Created user could not be loaded"));
    }

    private AuthResponse issueTokensForUser(User user) {
        setLastLogin(user);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    private void setLastLogin(User user) {
        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);
    }
}

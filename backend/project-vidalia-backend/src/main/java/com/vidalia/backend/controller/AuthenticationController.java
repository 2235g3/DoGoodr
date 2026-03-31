package com.vidalia.backend.controller;

import com.vidalia.backend.dto.auth.AuthResponse;
import com.vidalia.backend.dto.auth.LoginRequest;
import com.vidalia.backend.dto.auth.RefreshTokenRequest;
import com.vidalia.backend.dto.auth.RegisterRequest;
import com.vidalia.backend.model.Role;
import com.vidalia.backend.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authenticationService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authenticationService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/volunteer")
    public ResponseEntity<AuthResponse> registerVolunteer(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.registerUser(request, Role.VOLUNTEER);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/register/organisation")
    public ResponseEntity<AuthResponse> registerOrganisation(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.registerUser(request, Role.ORGANISATION);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


}

package com.vidalia.backend.controller;

import com.vidalia.backend.dto.user.UpdateUserDTO;
import com.vidalia.backend.dto.user.UpdateUserPasswordDTO;
import com.vidalia.backend.dto.user.UserResponseDTO;
import com.vidalia.backend.security.CustomUserDetails;
import com.vidalia.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getUser(@AuthenticationPrincipal CustomUserDetails userDetails) {

        UUID userId = userDetails.getId();
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);

    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER', 'ORGANISATION')")
    public ResponseEntity<UserResponseDTO> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(userService.updateUser(userId, updateUserDTO));
    }

    @PutMapping("/me/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER', 'ORGANISATION')")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody UpdateUserPasswordDTO updateUserPasswordDTO) {
        UUID userId = userDetails.getId();
        userService.updateUserPassword(userId, updateUserPasswordDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER', 'ORGANISATION')")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}

package com.vidalia.backend.controller;

import com.vidalia.backend.dto.user.CreateUserDTO;
import com.vidalia.backend.dto.user.UpdateUserDTO;
import com.vidalia.backend.dto.user.UpdateUserPasswordDTO;
import com.vidalia.backend.dto.user.UserResponseDTO;
import com.vidalia.backend.model.Role;
import com.vidalia.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/user")
@PreAuthorize("hasRole('ADMIN')")  // All endpoints require ADMIN role
public class UserAdminController {

    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/role")
    public ResponseEntity<List<UserResponseDTO>> getUsersByType(@RequestParam Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PostMapping("/")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(createUserDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        return ResponseEntity.ok(userService.updateUser(id, updateUserDTO));
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable UUID id, @Valid @RequestBody UpdateUserPasswordDTO updateUserPasswordDTO) {
        userService.updateUserPassword(id, updateUserPasswordDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

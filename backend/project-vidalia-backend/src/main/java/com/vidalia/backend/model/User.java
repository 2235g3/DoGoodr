package com.vidalia.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/*
    User entity representing the users of the system
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID id;

    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Email
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    @Column(nullable = false, length = 254) // Password pattern validation is in DTOs
    private String password;

    @Column(length = 254)
    @Email
    private String secondaryEmail;

    @Column(length = 16) //Phone number is stored with + sign
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column()
    private LocalDateTime lastLogin;

    @NotNull(message = "Role cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}

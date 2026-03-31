package com.vidalia.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Schema(description = "User's email address", example = "examplemail@mail.com")
    @Email(message = "Email should be valid")
    @Size(min = 3, max = 50, message = "Email must be between 3 and 50 characters")
    protected String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password must contain at least 1 letter, 1 number, and be at least 8 characters long", example = "Password123")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must be at least 8 characters long, contain at least 1 letter and 1 number")
    protected String password;

}

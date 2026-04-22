package com.vidalia.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VRegisterRequest {

    @NotBlank(message = "Email is required")
    @Schema(description = "User's email address", example = "examplemail@mail.com")
    @Email(message = "Email should be valid")
    @Size(min = 3, max = 254, message = "Email must be between 3 and 254 characters")
    protected String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 number, 1 symbol, and be at least 8 characters long", example = "Password123")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*[#?!@$%^&*-])[A-Za-z\\d#?!@$%^&*-]{8,}$",
            message = "Password must be at least 8 characters long, contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 symbol")
    protected String password;

    @NotNull(message = "Forename must not be null")
    @Size(min = 1, max = 50, message = "Forename must be between 1 and 50 characters")
    protected String forename;

    @NotNull(message = "Surname must not be null")
    @Size(min = 1, max = 50, message = "Surname must be between 1 and 50 characters")
    protected String surname;

    @NotBlank(message = "Prefered name must not be blank")
    protected String preferedName;

    @NotNull(message = "Date of Birth must not be null")
    protected LocalDate dateOfBirth;

}

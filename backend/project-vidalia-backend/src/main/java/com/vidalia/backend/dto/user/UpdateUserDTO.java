package com.vidalia.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {
    @Schema(description = "User's email address", example = "examplemail@mail.com")
    @Email(message = "Email should be valid")
    @Size(min = 3, max = 254, message = "Email must be between 3 and 254 characters")
    protected String email;

    @Schema(description = "User's email address", example = "examplemail@mail.com")
    @Email(message = "Email should be valid")
    @Size(min = 3, max = 50, message = "Email must be between 3 and 50 characters")
    protected String secondaryEmail;

    @Pattern(
            regexp = "^\\+[1-9]\\d{7,14}$",
            message = "Phone number must be in E.164 format (e.g. +44123123456)"
    )
    protected String phoneNumber;
}

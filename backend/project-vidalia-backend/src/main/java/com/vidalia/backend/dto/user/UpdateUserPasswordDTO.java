package com.vidalia.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserPasswordDTO {

    @NotBlank(message = "Old password is required")
    protected String oldPassword;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password must contain at least 1 letter, 1 number, and be at least 8 characters long", example = "Password123")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long, contain at least 1 letter, 1 number, and 1 special character")
    protected String newPassword;
}

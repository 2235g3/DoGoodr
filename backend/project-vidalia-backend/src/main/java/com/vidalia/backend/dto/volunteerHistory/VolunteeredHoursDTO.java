package com.vidalia.backend.dto.volunteerHistory;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VolunteeredHoursDTO {
    @NotNull(message = "Hours must not be null")
    @Min(value = 0, message = "Hours must be a non-negative number")
    @Max(value = 8, message = "Hours must not exceed 8 hours per session")
    private double hours;
}

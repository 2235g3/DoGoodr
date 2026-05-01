package com.vidalia.backend.dto.volunteerHistory;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateVolunteerHistoryDTO {
    @NotNull(message = "Volunteer profile ID must not be null")
    private UUID opportunityId;
    @NotNull(message = "Start date must not be null")
    private LocalDate startDate;
    @NotNull(message = "End date must not be null")
    private LocalDate endDate;

}

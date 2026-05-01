package com.vidalia.backend.dto.volunteerHistory;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateVolunteerHistoryDateRangeDTO {
    @NotNull(message = "Start date must not be null")
    private LocalDate startDate;
    @NotNull(message = "End date must not be null")
    private LocalDate endDate;
}

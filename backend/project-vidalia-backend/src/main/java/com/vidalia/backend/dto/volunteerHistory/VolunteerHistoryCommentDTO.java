package com.vidalia.backend.dto.volunteerHistory;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VolunteerHistoryCommentDTO {
    @NotNull(message = "Comment must not be null")
    private String comment;
}

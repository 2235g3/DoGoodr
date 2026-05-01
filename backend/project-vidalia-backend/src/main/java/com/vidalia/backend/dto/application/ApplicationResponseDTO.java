package com.vidalia.backend.dto.application;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ApplicationResponseDTO {

    private UUID id;
    private UUID volunteerId;
    private String volunteerName;
    private UUID opportunityId;
    private String opportunityName;
    private String message;
    private String status;
    private LocalDateTime dateApplied;
    private LocalDateTime decisionDate;

}

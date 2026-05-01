package com.vidalia.backend.dto.volunteerHistory;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class VolunteerHistoryResponseDTO {

    private long id;
    private UUID volunteerId;
    private String volunteerName;
    private UUID opportunityId;
    private String opportunityTitle;
    private UUID organisationId;
    private String organisationName;

    private double hoursLogged;
    private LocalDate startDate;
    private LocalDate endDate;
    private int pointsGained;
    private String organisationComment;
}

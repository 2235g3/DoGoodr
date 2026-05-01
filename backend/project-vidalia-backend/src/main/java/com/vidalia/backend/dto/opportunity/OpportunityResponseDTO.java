package com.vidalia.backend.dto.opportunity;

import com.vidalia.backend.model.OpportunityStatus;
import com.vidalia.backend.dto.profile.OProfileResponseDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OpportunityResponseDTO {
    private UUID id;
    private String title;
    private String description;
    private String location;
    private Double longitude;
    private Double latitude;
    private Boolean remote;
    private OpportunityStatus status;
    private Integer minAge;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean recurring;
    private String availability;
    private Integer requiredHours;
    private Integer capacity;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private OProfileResponseDTO organisationProfile;

}

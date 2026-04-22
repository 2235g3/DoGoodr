package com.vidalia.backend.dto.profile;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VProfileResponseDTO {
    private UUID id;
    private String forename;
    private String surname;
    private String preferredName;
    private String profilePictureUrl;
    private String cvUrl;
    private String contactEmail;
    private String location;
    private String profileDescription;
    private Double longitude;
    private Double latitude;
    private Integer maxTravelDistance;
    private boolean remoteOnly;
    private Integer totalHours;
    private String availability;
    private LocalDate dateOfBirth;
    private LocalDateTime lastUpdated;
    private Integer pointsBalance;


}

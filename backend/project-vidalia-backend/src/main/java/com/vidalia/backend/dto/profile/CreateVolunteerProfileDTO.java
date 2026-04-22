package com.vidalia.backend.dto.profile;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateVolunteerProfileDTO {
    private String forename;
    private String surname;
    private String preferredName;
    private String contactEmail;
    private String location;
    private String profileDescription;
    private Double longitude;
    private Double latitude;
    private Integer maxTravelDistance;
    private LocalDate dateOfBirth;
}

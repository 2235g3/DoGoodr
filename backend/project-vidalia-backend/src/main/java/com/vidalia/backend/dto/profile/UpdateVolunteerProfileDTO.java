package com.vidalia.backend.dto.profile;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateVolunteerProfileDTO {
    @Column(length = 100)
    @Size(min = 3, max = 100, message = "Name must be between 3 and 254 characters")
    private String forename;
    @Column(length = 100)
    private String surname;
    @Column(length = 100)
    private String preferredName;
    @Email
    @Column(length = 254)
    @Size(min = 3, max = 254, message = "Email must be between 3 and 254 characters")
    private String contactEmail;
    @Column(length = 255)
    private String location;
    @Column(length = 255)
    private String profileDescription;
    private Integer maxTravelDistance;
    @Column(length = 100)
    private String availability;
}

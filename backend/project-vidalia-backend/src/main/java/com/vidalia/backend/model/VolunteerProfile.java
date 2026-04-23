package com.vidalia.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "volunteer_profiles")
public class VolunteerProfile {

    @Id
    @GeneratedValue
    @Column(name = "v_profile_id", columnDefinition = "uuid")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 100)
    private String forename;

    @Column(length = 100)
    private String surname;

    @Column(name = "preferred_name", nullable = false, length = 100)
    private String preferredName;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Column(name = "cv_url", length = 500)
    private String cvUrl;

    @Email
    @Column(name = "contact_email", length = 254)
    private String contactEmail;

    @Column(length = 255)
    private String location;

    @Column(name = "profile_description", columnDefinition = "TEXT")
    private String profileDescription;

    @Column
    private Double longitude;

    @Column
    private Double latitude;

    @Column(name = "max_travel_distance")
    private Integer maxTravelDistance = 0;

    @Column(name = "remote_only")
    private boolean remoteOnly;

    @Column(name = "total_hours")
    private Integer totalHours = 0;

    @Column(length = 255)
    private String availability;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @Column(name = "points_balance", nullable = false)
    private Integer pointsBalance = 0;

    @PrePersist
    private void onCreate() {
        if (lastUpdated == null) {
            lastUpdated = LocalDateTime.now();
        }
    }

    @PreUpdate
    private void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

}

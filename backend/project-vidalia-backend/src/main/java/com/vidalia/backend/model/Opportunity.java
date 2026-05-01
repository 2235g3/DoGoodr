package com.vidalia.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "opportunities")
public class Opportunity {
    @Id
    @GeneratedValue
    @Column(name = "opportunity_id", columnDefinition = "uuid")
    private UUID id;

    @NotNull(message = "Title cannot be null")
    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false, length = 255)
    private String title;

    @NotNull(message = "Description cannot be null")
    @NotBlank(message = "Description cannot be blank")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String location;

    @Column
    private Double longitude;

    @Column
    private Double latitude;

    @Column(name = "remote", nullable = false)
    private boolean remote;

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OpportunityStatus status = OpportunityStatus.OPEN;

    @Min(value = 0, message = "Minimum age cannot be negative")
    @Max(value = 120, message = "Minimum age cannot exceed 120")
    @Column(name = "min_age")
    private Integer minAge;

    @NotNull(message = "Start date cannot be null")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "recurring")
    private Boolean recurring;

    @Column(length = 255)
    private String availability;

    @Min(value = 1, message = "Required hours must be at least 1")
    @Column(name = "required_hours")
    private Integer requiredHours;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(name = "capacity")
    private Integer capacity;

    @NotNull(message = "Creation date cannot be null")
    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @NotNull(message = "Last updated date cannot be null")
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @NotNull(message = "Organisation profile cannot be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organisation_profile_id", nullable = false)
    private OrganisationProfile organisationProfile;

    @PrePersist
    private void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        dateCreated = now;
        lastUpdated = now;
    }

    @PreUpdate
    private void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}

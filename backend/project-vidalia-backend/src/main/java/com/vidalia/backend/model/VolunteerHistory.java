package com.vidalia.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "volunteer_history")
public class VolunteerHistory {

    @Id
    @GeneratedValue
    @Column(name = "history_id")
    private long id;

    @NotNull(message = "Volunteer profile cannot be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "volunteer_profile_id", nullable = false)
    private VolunteerProfile volunteerProfile;

    @NotNull(message = "Opportunity cannot be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @NotNull
    @Column(name = "hours_logged", nullable = false)
    @Min(value = 0, message = "Hours logged cannot be negative")
    private double hoursLogged;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(name = "points_gained", nullable = false)
    private int pointsGained;

    @NotNull
    @Column(name = "organisation_comment")
    private String organisationComment;

}

package com.vidalia.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organisation_profiles")
public class OrganisationProfile {
    @Id
    @GeneratedValue
    @Column(name = "v_profile_id", columnDefinition = "uuid")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(name = "description")
    private String description;

    @Column(name = "contact_email")
    @Email
    private String contactEmail;

    @Column(name = "location")
    private String location;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "last_updated",  nullable = false)
    private LocalDateTime lastUpdated;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @OneToMany(mappedBy = "organisationProfile", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Opportunity> opportunities = new ArrayList<>();

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

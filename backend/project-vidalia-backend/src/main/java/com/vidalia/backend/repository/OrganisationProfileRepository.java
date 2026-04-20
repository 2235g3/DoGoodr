package com.vidalia.backend.repository;

import com.vidalia.backend.model.VolunteerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganisationProfileRepository extends JpaRepository<VolunteerProfile, UUID> {
    Optional<VolunteerProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}

package com.vidalia.backend.repository;

import com.vidalia.backend.model.OrganisationProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganisationProfileRepository extends JpaRepository<OrganisationProfile, UUID> {
    Optional<OrganisationProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}

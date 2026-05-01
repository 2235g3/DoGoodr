package com.vidalia.backend.repository;

import com.vidalia.backend.model.VolunteerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VolunteerProfileRepository extends JpaRepository<VolunteerProfile, UUID> {
    Optional<VolunteerProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}

package com.vidalia.backend.repository;

import com.vidalia.backend.model.Opportunity;
import com.vidalia.backend.model.OrganisationProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {
    List<Opportunity> findAllByOrganisationProfileId(UUID organisationId);

    Optional<Opportunity> findByIdAndOrganisationProfileId(UUID id, UUID organisationId);
}

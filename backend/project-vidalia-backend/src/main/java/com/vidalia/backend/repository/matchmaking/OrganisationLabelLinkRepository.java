package com.vidalia.backend.repository.matchmaking;

import com.vidalia.backend.model.matchmaking.OrganisationLabelKey;
import com.vidalia.backend.model.matchmaking.OrganisationLabelLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganisationLabelLinkRepository extends JpaRepository<OrganisationLabelLink, OrganisationLabelKey> {
    List<OrganisationLabelLink> findAllByOrganisationId(UUID organisationId);

    Optional<OrganisationLabelLink> findByOrganisationIdAndLabelId(UUID organisationId, Long labelId);

    void deleteAllByOrganisationId(UUID organisationId);

    void deleteAllByLabelId(Long id);
}


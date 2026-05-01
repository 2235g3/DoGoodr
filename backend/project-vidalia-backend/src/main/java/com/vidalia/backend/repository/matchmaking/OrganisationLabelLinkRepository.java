package com.vidalia.backend.repository.matchmaking;

import com.vidalia.backend.model.matchmaking.OrganisationLabelKey;
import com.vidalia.backend.model.matchmaking.OrganisationLabelLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganisationLabelLinkRepository extends JpaRepository<OrganisationLabelLink, OrganisationLabelKey> {
    @Query("SELECT link FROM OrganisationLabelLink link WHERE link.id.organisationId = :organisationId")
    List<OrganisationLabelLink> findAllByOrganisationId(@Param("organisationId") UUID organisationId);

    @Query("SELECT link FROM OrganisationLabelLink link WHERE link.id.organisationId = :organisationId AND link.id.labelId = :labelId")
    Optional<OrganisationLabelLink> findByOrganisationIdAndLabelId(@Param("organisationId") UUID organisationId,
                                                                   @Param("labelId") Long labelId);

    @Modifying
    @Query("DELETE FROM OrganisationLabelLink link WHERE link.id.organisationId = :organisationId")
    void deleteAllByOrganisationId(@Param("organisationId") UUID organisationId);

    @Modifying
    @Query("DELETE FROM OrganisationLabelLink link WHERE link.id.labelId = :id")
    void deleteAllByLabelId(@Param("id") Long id);
}

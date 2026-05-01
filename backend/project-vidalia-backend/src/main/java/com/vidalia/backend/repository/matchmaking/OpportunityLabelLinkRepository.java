package com.vidalia.backend.repository.matchmaking;

import com.vidalia.backend.model.matchmaking.OpportunityLabelKey;
import com.vidalia.backend.model.matchmaking.OpportunityLabelLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpportunityLabelLinkRepository extends JpaRepository<OpportunityLabelLink, OpportunityLabelKey> {
    @Query("SELECT link FROM OpportunityLabelLink link WHERE link.id.opportunityId = :opportunityId")
    List<OpportunityLabelLink> findAllByOpportunityId(@Param("opportunityId") UUID opportunityId);

    @Query("SELECT link FROM OpportunityLabelLink link WHERE link.id.opportunityId IN :opportunityIds")
    List<OpportunityLabelLink> findAllByOpportunityIdIn(@Param("opportunityIds") Collection<UUID> opportunityIds);

    @Query("SELECT link FROM OpportunityLabelLink link WHERE link.id.opportunityId = :opportunityId AND link.id.labelId = :labelId")
    Optional<OpportunityLabelLink> findByOpportunityIdAndLabelId(@Param("opportunityId") UUID opportunityId,
                                                                 @Param("labelId") Long labelId);

    @Modifying
    @Query("DELETE FROM OpportunityLabelLink link WHERE link.id.opportunityId = :opportunityId")
    void deleteAllByOpportunityId(@Param("opportunityId") UUID opportunityId);

    @Modifying
    @Query("DELETE FROM OpportunityLabelLink link WHERE link.id.labelId = :id")
    void deleteAllByLabelId(@Param("id") Long id);
}

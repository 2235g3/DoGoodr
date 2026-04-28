package com.vidalia.backend.repository.matchmaking;

import com.vidalia.backend.model.matchmaking.OpportunityLabelKey;
import com.vidalia.backend.model.matchmaking.OpportunityLabelLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpportunityLabelLinkRepository extends JpaRepository<OpportunityLabelLink, OpportunityLabelKey> {
    List<OpportunityLabelLink> findAllByOpportunityId(UUID opportunityId);

    List<OpportunityLabelLink> findAllByOpportunityIdIn(Collection<UUID> opportunityIds);

    Optional<OpportunityLabelLink> findByOpportunityIdAndLabelId(UUID opportunityId, Long labelId);

    void deleteAllByOpportunityId(UUID opportunityId);

    void deleteAllByLabelId(Long id);
}


package com.vidalia.backend.repository.matchmaking;

import com.vidalia.backend.model.matchmaking.SemanticLink;
import com.vidalia.backend.model.matchmaking.SemanticLinkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemanticLinkRepository extends JpaRepository<SemanticLink, SemanticLinkId> {
    List<SemanticLink> findAllByTagOne_IdOrTagTwo_Id(Long tagOneId, Long tagTwoId);

    boolean existsByTagOne_IdAndTagTwo_Id(Long tagOneId, Long tagTwoId);
}


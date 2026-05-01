package com.vidalia.backend.repository.matchmaking;

import com.vidalia.backend.model.matchmaking.SemanticLink;
import com.vidalia.backend.model.matchmaking.SemanticLinkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemanticLinkRepository extends JpaRepository<SemanticLink, SemanticLinkId> {
    List<SemanticLink> findAllByTagOne_IdOrTagTwo_Id(Long tagOneId, Long tagTwoId);
    Optional<SemanticLink> findByTagOne_IdAndTagTwo_Id(Long tagOneId, Long tagTwoId);
    boolean existsByTagOne_IdAndTagTwo_Id(Long tagOneId, Long tagTwoId);

    void deleteAllByTagOne_Id(Long id);
    void deleteAllByTagTwo_Id(Long id);
}


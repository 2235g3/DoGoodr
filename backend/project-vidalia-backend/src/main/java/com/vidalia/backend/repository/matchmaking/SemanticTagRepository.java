package com.vidalia.backend.repository.matchmaking;

import com.vidalia.backend.model.matchmaking.SemanticTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemanticTagRepository extends JpaRepository<SemanticTag, Long> {
    Optional<SemanticTag> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}


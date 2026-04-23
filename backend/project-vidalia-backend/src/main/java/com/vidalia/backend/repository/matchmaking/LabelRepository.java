package com.vidalia.backend.repository.matchmaking;

import com.vidalia.backend.model.matchmaking.Label;
import com.vidalia.backend.model.matchmaking.LabelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    Optional<Label> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Label> findAllByType(LabelType type);

    List<Label> findAllByTypeIn(Collection<LabelType> types);

    List<Label> findAllBySemanticTagId(Long semanticTagId);
}


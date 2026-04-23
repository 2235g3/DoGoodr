package com.vidalia.backend.repository.matchmaking;

import com.vidalia.backend.model.matchmaking.VolunteerLabelKey;
import com.vidalia.backend.model.matchmaking.VolunteerLabelLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VolunteerLabelLinkRepository extends JpaRepository<VolunteerLabelLink, VolunteerLabelKey> {
    List<VolunteerLabelLink> findAllByVolunteerId(UUID volunteerId);

    Optional<VolunteerLabelLink> findByVolunteerIdAndLabelId(UUID volunteerId, Long labelId);

    void deleteAllByVolunteerId(UUID volunteerId);
}


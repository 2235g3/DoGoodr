package com.vidalia.backend.repository.matchmaking;

import com.vidalia.backend.model.matchmaking.VolunteerLabelKey;
import com.vidalia.backend.model.matchmaking.VolunteerLabelLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VolunteerLabelLinkRepository extends JpaRepository<VolunteerLabelLink, VolunteerLabelKey> {
    @Query("SELECT link FROM VolunteerLabelLink link WHERE link.id.volunteerId = :volunteerId")
    List<VolunteerLabelLink> findAllByVolunteerId(@Param("volunteerId") UUID volunteerId);

    @Query("SELECT link FROM VolunteerLabelLink link WHERE link.id.volunteerId = :volunteerId AND link.id.labelId = :labelId")
    Optional<VolunteerLabelLink> findByVolunteerIdAndLabelId(@Param("volunteerId") UUID volunteerId,
                                                             @Param("labelId") Long labelId);

    @Modifying
    @Query("DELETE FROM VolunteerLabelLink link WHERE link.id.volunteerId = :volunteerId")
    void deleteAllByVolunteerId(@Param("volunteerId") UUID volunteerId);

    @Modifying
    @Query("DELETE FROM VolunteerLabelLink link WHERE link.id.labelId = :id")
    void deleteAllByLabelId(@Param("id") Long id);
}

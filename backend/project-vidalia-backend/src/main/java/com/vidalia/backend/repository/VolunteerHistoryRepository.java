package com.vidalia.backend.repository;

import com.vidalia.backend.model.VolunteerHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VolunteerHistoryRepository extends JpaRepository<VolunteerHistory, Long> {
    List<VolunteerHistory> findAllByVolunteerProfileId(UUID volunteerId);
}

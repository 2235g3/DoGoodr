package com.vidalia.backend.repository;

import com.vidalia.backend.model.VolunteerHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerHistoryRepository extends JpaRepository<VolunteerHistory, Long> {
}

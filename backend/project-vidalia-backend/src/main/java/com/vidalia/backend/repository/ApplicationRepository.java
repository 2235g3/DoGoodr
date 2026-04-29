package com.vidalia.backend.repository;

import com.vidalia.backend.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findAllByVolunteerProfileId(UUID volunteerId);
    List<Application> findAllByOpportunityId(UUID opportunityId);

}

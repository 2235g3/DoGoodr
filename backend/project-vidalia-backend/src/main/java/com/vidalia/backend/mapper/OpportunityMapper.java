package com.vidalia.backend.mapper;

import com.vidalia.backend.dto.opportunity.CreateOpportunityDTO;
import com.vidalia.backend.dto.opportunity.OpportunityResponseDTO;
import com.vidalia.backend.dto.opportunity.UpdateOpportunityDTO;
import com.vidalia.backend.model.Opportunity;
import com.vidalia.backend.model.OpportunityStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OpportunityMapper {

    public Opportunity toEntity(CreateOpportunityDTO dto) {
        Opportunity opportunity = new Opportunity();
        opportunity.setTitle(dto.getTitle());
        opportunity.setDescription(dto.getDescription());
        opportunity.setLocation(dto.getLocation());
        opportunity.setLongitude(dto.getLongitude());
        opportunity.setLatitude(dto.getLatitude());
        opportunity.setRemote(Boolean.TRUE.equals(dto.getRemote()));
        opportunity.setStatus(dto.getStatus() != null ? dto.getStatus() : OpportunityStatus.OPEN);
        opportunity.setMinAge(dto.getMinAge());
        opportunity.setStartDate(dto.getStartDate());
        opportunity.setEndDate(dto.getEndDate());
        opportunity.setRecurring(dto.getRecurring());
        opportunity.setRequiredHours(dto.getRequiredHours());
        opportunity.setCapacity(dto.getCapacity());
        return opportunity;
    }

    public OpportunityResponseDTO toDTO(Opportunity opportunity) {
        OpportunityResponseDTO dto = new OpportunityResponseDTO();
        dto.setId(opportunity.getId());
        dto.setTitle(opportunity.getTitle());
        dto.setDescription(opportunity.getDescription());
        dto.setLocation(opportunity.getLocation());
        dto.setLongitude(opportunity.getLongitude());
        dto.setLatitude(opportunity.getLatitude());
        dto.setRemote(opportunity.isRemote());
        dto.setStatus(opportunity.getStatus());
        dto.setMinAge(opportunity.getMinAge());
        dto.setStartDate(opportunity.getStartDate());
        dto.setEndDate(opportunity.getEndDate());
        dto.setRecurring(opportunity.getRecurring());
        dto.setRequiredHours(opportunity.getRequiredHours());
        dto.setCapacity(opportunity.getCapacity());
        dto.setDateCreated(opportunity.getDateCreated());
        dto.setLastUpdated(opportunity.getLastUpdated());
        dto.setOrganisationProfile(opportunity.getOrganisationProfile());
        return dto;
    }

    public void updateEntity(Opportunity opportunity, UpdateOpportunityDTO dto) {
        if (dto.getTitle() != null) {
            opportunity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            opportunity.setDescription(dto.getDescription());
        }
        if (dto.getLocation() != null) {
            opportunity.setLocation(dto.getLocation());
        }
        if (dto.getLongitude() != null) {
            opportunity.setLongitude(dto.getLongitude());
        }
        if (dto.getLatitude() != null) {
            opportunity.setLatitude(dto.getLatitude());
        }
        if (dto.getRemote() != null) {
            opportunity.setRemote(dto.getRemote());
        }
        if (dto.getStatus() != null) {
            opportunity.setStatus(dto.getStatus());
        }
        if (dto.getMinAge() != null) {
            opportunity.setMinAge(dto.getMinAge());
        }
        if (dto.getStartDate() != null) {
            opportunity.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            opportunity.setEndDate(dto.getEndDate());
        }
        if (dto.getRecurring() != null) {
            opportunity.setRecurring(dto.getRecurring());
        }
        if (dto.getRequiredHours() != null) {
            opportunity.setRequiredHours(dto.getRequiredHours());
        }
        if (dto.getCapacity() != null) {
            opportunity.setCapacity(dto.getCapacity());
        }

        opportunity.setLastUpdated(LocalDateTime.now());
    }
}

package com.vidalia.backend.service;

import com.vidalia.backend.dto.notification.CreateNotificationDTO;
import com.vidalia.backend.dto.volunteerHistory.*;
import com.vidalia.backend.exceptions.ForbiddenRequestException;
import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.mapper.VolunteerHistoryMapper;
import com.vidalia.backend.model.*;
import com.vidalia.backend.repository.ApplicationRepository;
import com.vidalia.backend.repository.OpportunityRepository;
import com.vidalia.backend.repository.VolunteerHistoryRepository;
import com.vidalia.backend.repository.VolunteerProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class VolunteerHistoryService {

    VolunteerHistoryRepository volunteerHistoryRepository;
    VolunteerHistoryMapper volunteerHistoryMapper;

    VolunteerProfileRepository volunteerProfileRepository;
    OpportunityRepository opportunityRepository;
    ApplicationRepository applicationRepository;
    NotificationService notificationService;


    @Transactional(readOnly = true)
    public List<VolunteerHistoryResponseDTO> getVolunteerHistoryForVolunteer(UUID volunteerId) {
        //Get the volunteer
        VolunteerProfile volunteer = volunteerProfileRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + volunteerId));

        //Get the volunteer history entries for the volunteer
        List<VolunteerHistory> volunteerHistoryEntries = volunteerHistoryRepository.findAllByVolunteerProfileId(volunteerId);
        //Map the volunteer history entries to DTOs
        return volunteerHistoryEntries.stream()
                .map(volunteerHistoryMapper::toDTO)
                .toList();

    }

    @Transactional(readOnly = true)
    public List<VolunteerHistoryResponseDTO> getVolunteerHistoryForOpportunityAndOrganisation(UUID opportunityId, UUID organisationId ) {
        //Verify that the opportunity exists and belongs to this organisation
        opportunityRepository.findByIdAndOrganisationProfileId(opportunityId, organisationId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + opportunityId + " for organisation with id: " + organisationId));

        //Get the volunteer history entries for the opportunity
        List<VolunteerHistory> volunteerHistoryEntries = volunteerHistoryRepository.findAll().stream()
                .filter(vh -> vh.getOpportunity() != null && vh.getOpportunity().getId().equals(opportunityId))
                .toList();
        //Map the volunteer history entries to DTOs
        return volunteerHistoryEntries.stream()
                .map(volunteerHistoryMapper::toDTO)
                .toList();

    }

    @Transactional
    public VolunteerHistoryResponseDTO createVolunteerHistoryEntry(CreateVolunteerHistoryDTO dto, UUID organisationId, UUID volunteerId) {
        //Verify that the opportunity exists and belongs to this organisation
        opportunityRepository.findByIdAndOrganisationProfileId(dto.getOpportunityId(), organisationId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + dto.getOpportunityId() + " for organisation with id: " + organisationId));

        //Verify that this volunteer has an accepted application for this opportunity
        List<Application> application = applicationRepository.findAllByVolunteerProfileId(volunteerId);
        boolean hasApprovedApplication = application.stream()
                .anyMatch(app -> app.getOpportunity().getId().equals(dto.getOpportunityId()) && app.getStatus() == ApplicationStatus.ACCEPTED);

        if (!hasApprovedApplication) {
            throw new ForbiddenRequestException("Volunteer with id: " + volunteerId + " does not have an accepted application for opportunity with id: " + dto.getOpportunityId());
        }

        VolunteerProfile volunteer = volunteerProfileRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + volunteerId));

        Opportunity opportunity = opportunityRepository.findById(dto.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + dto.getOpportunityId()));

        //Create a new volunteer history entry
        VolunteerHistory history = volunteerHistoryMapper.toEntity(dto);
        history.setVolunteerProfile(volunteer);
        history.setOpportunity(opportunity);
        sendCreatedNotificationToVolunteer(history);
        VolunteerHistory savedHistory = volunteerHistoryRepository.save(history);
        return volunteerHistoryMapper.toDTO(savedHistory);
    }

    @Transactional
    public VolunteerHistoryResponseDTO updateDateRange(long logId, UUID organisationId, UpdateVolunteerHistoryDateRangeDTO dto) {
        //Verify that the volunteer history entry exists and belongs to an opportunity of this organisation
        VolunteerHistory history = verifyVolunteerHistoryExistenceAndOwnership(logId, organisationId);

        //Update the date range
        history.setStartDate(dto.getStartDate());
        history.setEndDate(dto.getEndDate());
        sendUpdateNotificationToVolunteer(history);
        VolunteerHistory updatedHistory = volunteerHistoryRepository.save(history);
        return volunteerHistoryMapper.toDTO(updatedHistory);
    }

    @Transactional
    public VolunteerHistoryResponseDTO setLoggedMessage(VolunteerHistoryCommentDTO commentDTO, long logId, UUID organisationId) {
        //Verify that the volunteer history entry exists and belongs to an opportunity of this organisation
        VolunteerHistory history = verifyVolunteerHistoryExistenceAndOwnership(logId, organisationId);

        //Update the logged message
        history.setOrganisationComment(commentDTO.getComment());
        sendUpdateNotificationToVolunteer(history);
        VolunteerHistory updatedHistory = volunteerHistoryRepository.save(history);
        return volunteerHistoryMapper.toDTO(updatedHistory);
    }

    @Transactional
    public VolunteerHistoryResponseDTO addVolunteeredHours(VolunteeredHoursDTO dto, long logId, UUID organisationId) {
        //Verify that the volunteer history entry exists and belongs to an opportunity of this organisation
        VolunteerHistory history = verifyVolunteerHistoryExistenceAndOwnership(logId, organisationId);

        //Update the volunteered hours
        history.setHoursLogged(history.getHoursLogged() + dto.getHours());
        //Recalculate points based on the new total hours
        int newPoints = calculatePoints(history.getHoursLogged());
        updateVolunteerPoints(history, newPoints);
        history.setPointsEarned(newPoints);
        sendUpdateNotificationToVolunteer(history);
        VolunteerHistory updatedHistory = volunteerHistoryRepository.save(history);
        return volunteerHistoryMapper.toDTO(updatedHistory);
    }

    private VolunteerHistory verifyVolunteerHistoryExistenceAndOwnership(long logId, UUID organisationId) {
        VolunteerHistory history = volunteerHistoryRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer history entry not found with id: " + logId));
        if (history.getOpportunity() == null || history.getOpportunity().getOrganisationProfile() == null || !history.getOpportunity().getOrganisationProfile().getId().equals(organisationId)) {
            throw new ResourceNotFoundException("Volunteer history entry with id: " + logId + " does not belong to an opportunity of organisation with id: " + organisationId);
        }
        return history;
    }

    private int calculatePoints(double hours) {
        // Simple points calculation: 1 hour = 10 points
        return (int) (hours * 10);
    }

    private void updateVolunteerPoints(VolunteerHistory history, int newPoints) {
        VolunteerProfile volunteer = history.getVolunteerProfile();
        int currentPoints = volunteer.getPointsBalance();
        int updatedPoints = currentPoints + newPoints;
        volunteer.setPointsBalance(updatedPoints);
        volunteerProfileRepository.save(volunteer);
    }

    private void sendCreatedNotificationToVolunteer(VolunteerHistory history) {
        CreateNotificationDTO notificationDTO = new CreateNotificationDTO();
        notificationDTO.setRecipientId(history.getVolunteerProfile().getUser().getId());
        notificationDTO.setMessage("A new volunteering entry has been created for opportunity: " + history.getOpportunity().getTitle());
        notificationDTO.setType(NotificationType.VOLUNTEERING_HISTORY_UPDATED);
        notificationService.createNotification(notificationDTO);
    }

    private void sendUpdateNotificationToVolunteer(VolunteerHistory history) {
        CreateNotificationDTO notificationDTO = new CreateNotificationDTO();
        notificationDTO.setRecipientId(history.getVolunteerProfile().getUser().getId());
        notificationDTO.setMessage("Your volunteering entry for opportunity: " + history.getOpportunity().getTitle() + " has been updated.");
        notificationDTO.setType(NotificationType.VOLUNTEERING_HISTORY_UPDATED);
        notificationService.createNotification(notificationDTO);
    }
}

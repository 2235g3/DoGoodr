package com.vidalia.backend.service;

import com.vidalia.backend.dto.application.ApplicationResponseDTO;
import com.vidalia.backend.dto.application.CreateApplicationDTO;
import com.vidalia.backend.dto.notification.CreateNotificationDTO;
import com.vidalia.backend.exceptions.ForbiddenRequestException;
import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.mapper.ApplicationMapper;
import com.vidalia.backend.model.*;
import com.vidalia.backend.model.OpportunityStatus;
import com.vidalia.backend.repository.ApplicationRepository;
import com.vidalia.backend.repository.OpportunityRepository;
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
public class ApplicationService {

    ApplicationRepository applicationRepository;
    ApplicationMapper applicationMapper;

    OpportunityRepository opportunityRepository;
    VolunteerProfileRepository volunteerProfileRepository;

    NotificationService notificationService;


    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(applicationMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getAllApplicationsForVolunteer(UUID volunteerId) {
        return applicationRepository.findAllByVolunteerProfileId(volunteerId).stream()
                .map(applicationMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getAllApplicationsForVolunteerUser(UUID userId) {
        VolunteerProfile profile = getVolunteerProfileByUserId(userId);
        return getAllApplicationsForVolunteer(profile.getId());
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getAllApplicationsForOpportunityByOrganisation(UUID organisationId, UUID opportunityId) {
        // Validate that the opportunity belongs to the organisation
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + opportunityId));

        if (!opportunity.getOrganisationProfile().getId().equals(organisationId)) {
            throw new ForbiddenRequestException("Opportunity with id: " + opportunityId + " does not belong to organisation with id: " + organisationId);
        }

        return applicationRepository.findAllByOpportunityId(opportunityId).stream()
                .map(applicationMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getAllApplicationsForOpportunity(UUID opportunityId) {
        return applicationRepository.findAllByOpportunityId(opportunityId).stream()
                .map(applicationMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getAllApplicationsForOrganisation(UUID organisationId) {
        List<Opportunity> opportunities = opportunityRepository.findAllByOrganisationProfileId(organisationId);
        return opportunities.stream()
                .flatMap(opportunity -> applicationRepository.findAllByOpportunityId(opportunity.getId()).stream())
                .map(applicationMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ApplicationResponseDTO getApplicationById(UUID applicationId) {
        return applicationRepository.findById(applicationId)
                .map(applicationMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
    }

    @Transactional
    public ApplicationResponseDTO createApplication(CreateApplicationDTO createApplicationDTO, UUID volunteerId, UUID opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + opportunityId));

        // If the opportunity is not open, throw an exception
        if (opportunity.getStatus() != OpportunityStatus.OPEN) {
            throw new ForbiddenRequestException("Cannot apply to opportunity with id: " + opportunityId + " because it is not open for applications");
        }

        // Get volunteer profile
        VolunteerProfile profile = volunteerProfileRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found with id: " + volunteerId));

        Application application = applicationMapper.toEntity(profile, opportunity, createApplicationDTO);
        Application savedApplication = applicationRepository.save(application);
        ApplicationResponseDTO responseDTO = applicationMapper.toDTO(savedApplication);
        sendApplicationNotificationToOrganisation(responseDTO, opportunity);
        return responseDTO;
    }

    @Transactional
    public ApplicationResponseDTO createApplicationForVolunteerUser(CreateApplicationDTO createApplicationDTO, UUID userId, UUID opportunityId) {
        VolunteerProfile profile = getVolunteerProfileByUserId(userId);
        return createApplication(createApplicationDTO, profile.getId(), opportunityId);
    }

    @Transactional
    public ApplicationResponseDTO updateApplicationStatus(UUID organisationId, UUID applicationId, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        // Validate that the application belongs to an opportunity of the organisation
        if (!application.getOpportunity().getOrganisationProfile().getId().equals(organisationId)) {
            throw new ForbiddenRequestException("Application with id: " + applicationId + " does not belong to an opportunity of organisation with id: " + organisationId);
        }

        application.setStatus(newStatus);
        Application updatedApplication = applicationRepository.save(application);
        ApplicationResponseDTO responseDTO = applicationMapper.toDTO(updatedApplication);
        sendApplicationStatusChangeNotificationToVolunteer(responseDTO, application);
        return responseDTO;
    }

    private VolunteerProfile getVolunteerProfileByUserId(UUID userId) {
        return volunteerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found for user id: " + userId));
    }

    private void sendApplicationNotificationToOrganisation(ApplicationResponseDTO applicationDTO, Opportunity opportunity) {
        UUID organisationUserId = opportunity.getOrganisationProfile().getUser().getId();
        CreateNotificationDTO notificationDTO = new CreateNotificationDTO();
        notificationDTO.setRecipientId(organisationUserId);
        notificationDTO.setType(NotificationType.APPLICATION_RECEIVED);
        notificationDTO.setMessage("New application received for opportunity: " + applicationDTO.getOpportunityName());
        notificationService.createNotification(notificationDTO);
    }

    private void sendApplicationStatusChangeNotificationToVolunteer(ApplicationResponseDTO applicationDTO, Application application) {
        UUID volunteerUserId = application.getVolunteerProfile().getUser().getId();
        CreateNotificationDTO notificationDTO = new CreateNotificationDTO();
        notificationDTO.setRecipientId(volunteerUserId);
        notificationDTO.setType(NotificationType.DECISION_RECEIVED);
        notificationDTO.setMessage("The status of your application for opportunity: " + applicationDTO.getOpportunityName() + " has been updated.");
        notificationService.createNotification(notificationDTO);
    }


}

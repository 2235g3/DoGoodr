package com.vidalia.backend.service;

import com.vidalia.backend.dto.volunteerHistory.CreateVolunteerHistoryDTO;
import com.vidalia.backend.dto.volunteerHistory.VolunteeredHoursDTO;
import com.vidalia.backend.exceptions.ResourceAlreadyExistsException;
import com.vidalia.backend.mapper.VolunteerHistoryMapper;
import com.vidalia.backend.model.Application;
import com.vidalia.backend.model.ApplicationStatus;
import com.vidalia.backend.model.Opportunity;
import com.vidalia.backend.model.OrganisationProfile;
import com.vidalia.backend.model.Role;
import com.vidalia.backend.model.User;
import com.vidalia.backend.model.VolunteerHistory;
import com.vidalia.backend.model.VolunteerProfile;
import com.vidalia.backend.repository.ApplicationRepository;
import com.vidalia.backend.repository.OpportunityRepository;
import com.vidalia.backend.repository.VolunteerHistoryRepository;
import com.vidalia.backend.repository.VolunteerProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VolunteerHistoryServiceTest {

    @Mock
    private VolunteerHistoryRepository volunteerHistoryRepository;
    @Mock
    private VolunteerProfileRepository volunteerProfileRepository;
    @Mock
    private OpportunityRepository opportunityRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private NotificationService notificationService;

    private VolunteerHistoryService volunteerHistoryService;

    @BeforeEach
    void setUp() {
        volunteerHistoryService = new VolunteerHistoryService(
                volunteerHistoryRepository,
                new VolunteerHistoryMapper(),
                volunteerProfileRepository,
                opportunityRepository,
                applicationRepository,
                notificationService
        );
    }

    @Test
    void createVolunteerHistoryEntry_initialisesRequiredFieldsAndCompletesApplication() {
        UUID organisationId = UUID.randomUUID();
        UUID volunteerId = UUID.randomUUID();
        UUID opportunityId = UUID.randomUUID();

        VolunteerProfile volunteer = volunteerProfile(volunteerId);
        Opportunity opportunity = opportunity(opportunityId, organisationId);
        Application application = new Application();
        application.setVolunteerProfile(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.ACCEPTED);

        CreateVolunteerHistoryDTO dto = new CreateVolunteerHistoryDTO();
        dto.setOpportunityId(opportunityId);
        dto.setStartDate(LocalDate.of(2026, 4, 1));
        dto.setEndDate(LocalDate.of(2026, 4, 2));

        when(opportunityRepository.findByIdAndOrganisationProfileId(opportunityId, organisationId)).thenReturn(Optional.of(opportunity));
        when(volunteerHistoryRepository.existsByVolunteerProfileIdAndOpportunityId(volunteerId, opportunityId)).thenReturn(false);
        when(applicationRepository.findAllByVolunteerProfileId(volunteerId)).thenReturn(List.of(application));
        when(volunteerProfileRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));
        when(opportunityRepository.findById(opportunityId)).thenReturn(Optional.of(opportunity));
        when(volunteerHistoryRepository.save(any(VolunteerHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        volunteerHistoryService.createVolunteerHistoryEntry(dto, organisationId, volunteerId);

        ArgumentCaptor<VolunteerHistory> historyCaptor = ArgumentCaptor.forClass(VolunteerHistory.class);
        verify(volunteerHistoryRepository).save(historyCaptor.capture());
        VolunteerHistory savedHistory = historyCaptor.getValue();
        assertEquals("", savedHistory.getOrganisationComment());
        assertEquals(0, savedHistory.getHoursLogged());
        assertEquals(0, savedHistory.getPointsEarned());
        assertEquals(ApplicationStatus.COMPLETED, application.getStatus());
        verify(applicationRepository).save(application);
    }

    @Test
    void createVolunteerHistoryEntry_rejectsDuplicateVolunteerOpportunityHistory() {
        UUID organisationId = UUID.randomUUID();
        UUID volunteerId = UUID.randomUUID();
        UUID opportunityId = UUID.randomUUID();
        Opportunity opportunity = opportunity(opportunityId, organisationId);

        CreateVolunteerHistoryDTO dto = new CreateVolunteerHistoryDTO();
        dto.setOpportunityId(opportunityId);
        dto.setStartDate(LocalDate.of(2026, 4, 1));
        dto.setEndDate(LocalDate.of(2026, 4, 2));

        when(opportunityRepository.findByIdAndOrganisationProfileId(opportunityId, organisationId)).thenReturn(Optional.of(opportunity));
        when(volunteerHistoryRepository.existsByVolunteerProfileIdAndOpportunityId(volunteerId, opportunityId)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> volunteerHistoryService.createVolunteerHistoryEntry(dto, organisationId, volunteerId));
        verify(volunteerHistoryRepository, never()).save(any());
    }

    @Test
    void addVolunteeredHoursCreditsOnlyNewPoints() {
        UUID organisationId = UUID.randomUUID();
        UUID volunteerId = UUID.randomUUID();
        UUID opportunityId = UUID.randomUUID();
        VolunteerProfile volunteer = volunteerProfile(volunteerId);
        volunteer.setPointsBalance(20);
        volunteer.setTotalHours(2);

        VolunteerHistory history = new VolunteerHistory();
        history.setVolunteerProfile(volunteer);
        history.setOpportunity(opportunity(opportunityId, organisationId));
        history.setHoursLogged(2);
        history.setPointsEarned(20);
        history.setStartDate(LocalDate.of(2026, 4, 1));
        history.setEndDate(LocalDate.of(2026, 4, 2));
        history.setOrganisationComment("");

        VolunteeredHoursDTO dto = new VolunteeredHoursDTO();
        dto.setHours(1.5);

        when(volunteerHistoryRepository.findById(7L)).thenReturn(Optional.of(history));
        when(volunteerHistoryRepository.save(history)).thenReturn(history);

        volunteerHistoryService.addVolunteeredHours(dto, 7L, organisationId);

        assertEquals(3.5, history.getHoursLogged());
        assertEquals(35, history.getPointsEarned());
        assertEquals(35, volunteer.getPointsBalance());
        assertEquals(3, volunteer.getTotalHours());
    }

    private VolunteerProfile volunteerProfile(UUID volunteerId) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("volunteer@example.test");
        user.setRole(Role.VOLUNTEER);

        VolunteerProfile volunteer = new VolunteerProfile();
        volunteer.setId(volunteerId);
        volunteer.setUser(user);
        volunteer.setForename("Val");
        volunteer.setPreferredName("Val");
        volunteer.setDateOfBirth(LocalDate.of(2000, 1, 1));
        volunteer.setPointsBalance(0);
        volunteer.setTotalHours(0);
        return volunteer;
    }

    private Opportunity opportunity(UUID opportunityId, UUID organisationId) {
        OrganisationProfile organisation = new OrganisationProfile();
        organisation.setId(organisationId);
        organisation.setDisplayName("Community Kitchen");

        Opportunity opportunity = new Opportunity();
        opportunity.setId(opportunityId);
        opportunity.setTitle("Kitchen support");
        opportunity.setOrganisationProfile(organisation);
        return opportunity;
    }
}

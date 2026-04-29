package com.vidalia.backend.matching.service;

import com.vidalia.backend.matching.internal.MatchLabel;
import com.vidalia.backend.matching.internal.OpportunityMatchCandidate;
import com.vidalia.backend.model.Opportunity;
import com.vidalia.backend.model.VolunteerProfile;
import com.vidalia.backend.model.matchmaking.Label;
import com.vidalia.backend.model.matchmaking.LabelType;
import com.vidalia.backend.model.matchmaking.OpportunityLabelLink;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchingServiceTests {

    private final MatchingService matchingService = new MatchingService(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
    );

    @Test
    void filterCandidatesExcludesInPersonOpportunityForRemoteOnlyVolunteer() {
        VolunteerProfile volunteer = volunteer();
        volunteer.setRemoteOnly(true);

        Opportunity opportunity = opportunity();
        opportunity.setRemote(false);

        List<OpportunityMatchCandidate> filtered = matchingService.filterCandidates(
                volunteer,
                Map.of(),
                List.of(candidate(opportunity)),
                LocalDate.of(2026, 4, 29)
        );

        assertTrue(filtered.isEmpty());
    }

    @Test
    void filterCandidatesExcludesVolunteerWhoIsYoungerThanMinAge() {
        VolunteerProfile volunteer = volunteer();
        volunteer.setDateOfBirth(LocalDate.of(2010, 4, 30));

        Opportunity opportunity = opportunity();
        opportunity.setMinAge(18);

        List<OpportunityMatchCandidate> filtered = matchingService.filterCandidates(
                volunteer,
                Map.of(),
                List.of(candidate(opportunity)),
                LocalDate.of(2026, 4, 29)
        );

        assertTrue(filtered.isEmpty());
    }

    @Test
    void filterCandidatesExcludesOpportunityWhenRequiredLabelIsMissing() {
        VolunteerProfile volunteer = volunteer();
        Opportunity opportunity = opportunity();
        Label requiredLabel = label(100L, "Driving Licence", LabelType.OTHER, true);
        OpportunityLabelLink requiredLink = new OpportunityLabelLink(opportunity.getId(), requiredLabel, 1.0d);

        List<OpportunityMatchCandidate> filtered = matchingService.filterCandidates(
                volunteer,
                Map.of(),
                List.of(candidate(opportunity, requiredLink)),
                LocalDate.of(2026, 4, 29)
        );

        assertTrue(filtered.isEmpty());
    }

    @Test
    void filterCandidatesExcludesOpportunityWhenAvailabilityDoesNotOverlap() {
        VolunteerProfile volunteer = volunteer();
        volunteer.setAvailability("WEEKDAY_EVENING");

        Opportunity opportunity = opportunity();
        opportunity.setAvailability("WEEKEND_MORNING");

        List<OpportunityMatchCandidate> filtered = matchingService.filterCandidates(
                volunteer,
                Map.of(),
                List.of(candidate(opportunity)),
                LocalDate.of(2026, 4, 29)
        );

        assertTrue(filtered.isEmpty());
    }

    @Test
    void filterCandidatesExcludesOpportunityWhenDistanceExceedsTravelLimit() {
        VolunteerProfile volunteer = volunteer();
        volunteer.setLatitude(51.5074d);
        volunteer.setLongitude(-0.1278d);
        volunteer.setMaxTravelDistance(5);

        Opportunity opportunity = opportunity();
        opportunity.setRemote(false);
        opportunity.setLatitude(51.6174d);
        opportunity.setLongitude(-0.1278d);

        List<OpportunityMatchCandidate> filtered = matchingService.filterCandidates(
                volunteer,
                Map.of(),
                List.of(candidate(opportunity)),
                LocalDate.of(2026, 4, 29)
        );

        assertTrue(filtered.isEmpty());
    }

    @Test
    void filterCandidatesKeepsOpportunityWhenRulesAreSatisfied() {
        VolunteerProfile volunteer = volunteer();
        volunteer.setAvailability("WEEKDAY_EVENING,WEEKEND_AFTERNOON");
        volunteer.setDateOfBirth(LocalDate.of(2000, 1, 1));
        volunteer.setLatitude(51.5074d);
        volunteer.setLongitude(-0.1278d);
        volunteer.setMaxTravelDistance(20);

        Opportunity opportunity = opportunity();
        opportunity.setMinAge(16);
        opportunity.setAvailability("WEEKDAY_EVENING");
        opportunity.setRemote(false);
        opportunity.setLatitude(51.5174d);
        opportunity.setLongitude(-0.1278d);

        Label requiredLabel = label(100L, "English", LabelType.LANGUAGE, true);
        OpportunityLabelLink requiredLink = new OpportunityLabelLink(opportunity.getId(), requiredLabel, 1.0d);
        MatchLabel volunteerLabel = MatchLabel.fromLabel(requiredLabel, 0.9d, true);

        List<OpportunityMatchCandidate> filtered = matchingService.filterCandidates(
                volunteer,
                Map.of(requiredLabel.getId(), volunteerLabel),
                List.of(candidate(opportunity, requiredLink)),
                LocalDate.of(2026, 4, 29)
        );

        assertFalse(filtered.isEmpty());
    }

    private VolunteerProfile volunteer() {
        VolunteerProfile volunteer = new VolunteerProfile();
        volunteer.setId(UUID.randomUUID());
        volunteer.setDateOfBirth(LocalDate.of(2000, 1, 1));
        volunteer.setRemoteOnly(false);
        volunteer.setMaxTravelDistance(10);
        return volunteer;
    }

    private Opportunity opportunity() {
        Opportunity opportunity = new Opportunity();
        opportunity.setId(UUID.randomUUID());
        opportunity.setRemote(false);
        return opportunity;
    }

    private OpportunityMatchCandidate candidate(Opportunity opportunity, OpportunityLabelLink... links) {
        Map<Long, OpportunityLabelLink> labelsById = new java.util.LinkedHashMap<>();
        for (OpportunityLabelLink link : links) {
            labelsById.put(link.getLabel().getId(), link);
        }
        return new OpportunityMatchCandidate(opportunity, labelsById);
    }

    private Label label(Long id, String name, LabelType type, boolean required) {
        Label label = new Label();
        label.setId(id);
        label.setName(name);
        label.setType(type);
        label.setRequired(required);
        return label;
    }
}

package com.vidalia.backend.matching.service;

import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.matching.dto.MatchedOpportunityDTO;
import com.vidalia.backend.matching.internal.MatchLabel;
import com.vidalia.backend.matching.internal.OpportunityMatchCandidate;
import com.vidalia.backend.mapper.OpportunityMapper;
import com.vidalia.backend.model.Opportunity;
import com.vidalia.backend.model.OpportunityStatus;
import com.vidalia.backend.model.VolunteerProfile;
import com.vidalia.backend.model.matchmaking.LabelType;
import com.vidalia.backend.model.matchmaking.OpportunityLabelLink;
import com.vidalia.backend.repository.OpportunityRepository;
import com.vidalia.backend.repository.VolunteerProfileRepository;
import com.vidalia.backend.repository.matchmaking.LabelRepository;
import com.vidalia.backend.repository.matchmaking.OpportunityLabelLinkRepository;
import com.vidalia.backend.repository.matchmaking.SemanticLinkRepository;
import com.vidalia.backend.repository.matchmaking.VolunteerLabelLinkRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MatchingService {

    static final double MIN_VIABLE_OPPORTUNITY_SCORE = 0.30d;

    private static final Set<LabelType> SCOREABLE_LABEL_TYPES = Set.of(
            LabelType.SKILL,
            LabelType.INTEREST,
            LabelType.CAUSE
    );

    private static final Map<LabelType, Double> TYPE_MULTIPLIERS = createTypeMultipliers();

    private final VolunteerProfileRepository volunteerProfileRepository;
    private final OpportunityRepository opportunityRepository;
    private final VolunteerLabelLinkRepository volunteerLabelLinkRepository;
    private final OpportunityLabelLinkRepository opportunityLabelLinkRepository;
    private final LabelRepository labelRepository;
    private final SemanticLinkRepository semanticLinkRepository;
    private final OpportunityMapper opportunityMapper;
    private final VolunteerMatchProfileBuilder volunteerMatchProfileBuilder;

    public List<MatchedOpportunityDTO> getMatchesForVolunteer(UUID volunteerId) {
        VolunteerProfile volunteer = loadVolunteer(volunteerId);
        Map<Long, MatchLabel> volunteerLabels = buildVolunteerLabels(volunteerId);
        List<OpportunityMatchCandidate> candidates = loadOpportunityCandidates();
        List<OpportunityMatchCandidate> filteredCandidates = filterCandidates(
                volunteer,
                volunteerLabels,
                candidates,
                LocalDate.now()
        );
        List<MatchedOpportunityDTO> scoredMatches = scoreCandidates(volunteer, volunteerLabels, filteredCandidates);

        return scoredMatches.stream()
                .filter(match -> match.getFinalScore() >= MIN_VIABLE_OPPORTUNITY_SCORE)
                .sorted(Comparator.comparingDouble(MatchedOpportunityDTO::getFinalScore).reversed())
                .toList();
    }

    VolunteerProfile loadVolunteer(UUID volunteerId) {
        return volunteerProfileRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found with id: " + volunteerId));
    }

    Map<Long, MatchLabel> buildVolunteerLabels(UUID volunteerId) {
        return volunteerMatchProfileBuilder.buildAllLabels(
                volunteerLabelLinkRepository.findAllByVolunteerId(volunteerId),
                labelRepository.findAll(),
                semanticLinkRepository.findAll()
        );
    }

    List<OpportunityMatchCandidate> loadOpportunityCandidates() {
        List<Opportunity> opportunities = opportunityRepository.findAll().stream()
                .filter(opportunity -> opportunity.getStatus() == OpportunityStatus.OPEN)
                .toList();

        Map<UUID, Map<Long, OpportunityLabelLink>> labelsByOpportunityId = new LinkedHashMap<>();
        List<UUID> opportunityIds = opportunities.stream()
                .map(Opportunity::getId)
                .toList();

        if (!opportunityIds.isEmpty()) {
            for (OpportunityLabelLink link : opportunityLabelLinkRepository.findAllByOpportunityIdIn(opportunityIds)) {
                if (link.getOpportunityId() == null || link.getLabel() == null || link.getLabel().getId() == null) {
                    continue;
                }
                labelsByOpportunityId
                        .computeIfAbsent(link.getOpportunityId(), ignored -> new LinkedHashMap<>())
                        .put(link.getLabel().getId(), link);
            }
        }

        return opportunities.stream()
                .map(opportunity -> new OpportunityMatchCandidate(
                        opportunity,
                        labelsByOpportunityId.getOrDefault(opportunity.getId(), Map.of())
                ))
                .toList();
    }

    List<OpportunityMatchCandidate> filterCandidates(VolunteerProfile volunteer,
                                                     Map<Long, MatchLabel> volunteerLabels,
                                                     List<OpportunityMatchCandidate> candidates,
                                                     LocalDate today) {
        return candidates.stream()
                .filter(candidate -> passesRemoteRule(volunteer, candidate.opportunity()))
                .filter(candidate -> passesMinAgeRule(volunteer, candidate.opportunity(), today))
                .filter(candidate -> passesRequiredLabelsRule(volunteerLabels, candidate))
                .filter(candidate -> passesAvailabilityRule(volunteer, candidate.opportunity()))
                .filter(candidate -> passesDistanceRule(volunteer, candidate.opportunity()))
                .toList();
    }

    boolean passesRemoteRule(VolunteerProfile volunteer, Opportunity opportunity) {
        return !volunteer.isRemoteOnly() || opportunity.isRemote();
    }

    boolean passesMinAgeRule(VolunteerProfile volunteer, Opportunity opportunity, LocalDate today) {
        if (opportunity.getMinAge() == null || volunteer.getDateOfBirth() == null) {
            return true;
        }
        int volunteerAge = Period.between(volunteer.getDateOfBirth(), today).getYears();
        return volunteerAge >= opportunity.getMinAge();
    }

    boolean passesRequiredLabelsRule(Map<Long, MatchLabel> volunteerLabels, OpportunityMatchCandidate candidate) {
        for (OpportunityLabelLink link : candidate.labelsById().values()) {
            if (link.getLabel() != null && link.getLabel().isRequired() && !volunteerLabels.containsKey(link.getLabel().getId())) {
                return false;
            }
        }
        return true;
    }

    boolean passesAvailabilityRule(VolunteerProfile volunteer, Opportunity opportunity) {
        Set<String> volunteerAvailability = parseAvailabilityTokens(volunteer.getAvailability());
        Set<String> opportunityAvailability = parseAvailabilityTokens(opportunity.getAvailability());

        if (volunteerAvailability.isEmpty() || opportunityAvailability.isEmpty()) {
            return true;
        }

        return volunteerAvailability.stream().anyMatch(opportunityAvailability::contains);
    }

    boolean passesDistanceRule(VolunteerProfile volunteer, Opportunity opportunity) {
        if (opportunity.isRemote()) {
            return true;
        }
        if (volunteer.isRemoteOnly()) {
            return true;
        }
        if (volunteer.getMaxTravelDistance() == null || volunteer.getMaxTravelDistance() <= 0) {
            return true;
        }
        if (volunteer.getLatitude() == null || volunteer.getLongitude() == null) {
            return true;
        }
        if (opportunity.getLatitude() == null || opportunity.getLongitude() == null) {
            return true;
        }

        double distanceKm = distanceKm(
                volunteer.getLatitude(),
                volunteer.getLongitude(),
                opportunity.getLatitude(),
                opportunity.getLongitude()
        );
        return distanceKm <= volunteer.getMaxTravelDistance();
    }

    List<MatchedOpportunityDTO> scoreCandidates(VolunteerProfile volunteer,
                                                Map<Long, MatchLabel> volunteerLabels,
                                                List<OpportunityMatchCandidate> candidates) {
        return candidates.stream()
                .map(candidate -> scoreCandidate(volunteer, volunteerLabels, candidate))
                .toList();
    }

    MatchedOpportunityDTO scoreCandidate(VolunteerProfile volunteer,
                                         Map<Long, MatchLabel> volunteerLabels,
                                         OpportunityMatchCandidate candidate) {
        double rawScore = 0d;
        double opportunityLabelSum = 0d;

        for (OpportunityLabelLink link : candidate.labelsById().values()) {
            if (!isScoreable(link)) {
                continue;
            }

            double typeMultiplier = TYPE_MULTIPLIERS.getOrDefault(link.getLabel().getType(), 0d);
            double weightedOpportunityLabel = link.getWeight() * typeMultiplier;
            opportunityLabelSum += weightedOpportunityLabel;

            MatchLabel volunteerLabel = volunteerLabels.get(link.getLabel().getId());
            if (volunteerLabel != null) {
                rawScore += volunteerLabel.weight() * weightedOpportunityLabel;
            }
        }

        double normalizedScore = opportunityLabelSum > 0d ? rawScore / opportunityLabelSum : 0d;
        MatchedOpportunityDTO dto = new MatchedOpportunityDTO();
        dto.setOpportunity(opportunityMapper.toDTO(candidate.opportunity()));
        dto.setFinalScore(normalizedScore);
        dto.setNormalizedScore(normalizedScore);
        dto.setDistanceKm(calculateDistanceKm(volunteer, candidate.opportunity()));
        return dto;
    }

    private Set<String> parseAvailabilityTokens(String rawAvailability) {
        if (rawAvailability == null || rawAvailability.isBlank()) {
            return Set.of();
        }

        return rawAvailability.lines()
                .flatMap(line -> List.of(line.split(",")).stream())
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadiusKm = 6371.0d;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    private boolean isScoreable(OpportunityLabelLink link) {
        return link.getLabel() != null
                && !link.getLabel().isRequired()
                && SCOREABLE_LABEL_TYPES.contains(link.getLabel().getType());
    }

    private Double calculateDistanceKm(VolunteerProfile volunteer, Opportunity opportunity) {
        if (volunteer.getLatitude() == null || volunteer.getLongitude() == null) {
            return null;
        }
        if (opportunity.getLatitude() == null || opportunity.getLongitude() == null) {
            return null;
        }
        return distanceKm(
                volunteer.getLatitude(),
                volunteer.getLongitude(),
                opportunity.getLatitude(),
                opportunity.getLongitude()
        );
    }

    private static Map<LabelType, Double> createTypeMultipliers() {
        Map<LabelType, Double> multipliers = new EnumMap<>(LabelType.class);
        multipliers.put(LabelType.SKILL, 0.30d);
        multipliers.put(LabelType.INTEREST, 0.25d);
        multipliers.put(LabelType.CAUSE, 0.20d);
        return Map.copyOf(multipliers);
    }
}

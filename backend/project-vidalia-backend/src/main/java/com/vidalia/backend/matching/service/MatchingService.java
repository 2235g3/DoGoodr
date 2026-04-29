package com.vidalia.backend.matching.service;

import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.matching.dto.MatchedOpportunityDTO;
import com.vidalia.backend.matching.internal.MatchLabel;
import com.vidalia.backend.model.VolunteerProfile;
import com.vidalia.backend.repository.VolunteerProfileRepository;
import com.vidalia.backend.repository.matchmaking.LabelRepository;
import com.vidalia.backend.repository.matchmaking.SemanticLinkRepository;
import com.vidalia.backend.repository.matchmaking.VolunteerLabelLinkRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MatchingService {

    private final VolunteerProfileRepository volunteerProfileRepository;
    private final VolunteerLabelLinkRepository volunteerLabelLinkRepository;
    private final LabelRepository labelRepository;
    private final SemanticLinkRepository semanticLinkRepository;
    private final VolunteerMatchProfileBuilder volunteerMatchProfileBuilder;

    public List<MatchedOpportunityDTO> getMatchesForVolunteer(UUID volunteerId) {
        VolunteerProfile volunteer = loadVolunteer(volunteerId);
        Map<Long, MatchLabel> volunteerLabels = buildVolunteerLabels(volunteerId);

        //TODO: Implement matching algorithm to find and score opportunities based on volunteer and volunteerLabels.
        return List.of();
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
}

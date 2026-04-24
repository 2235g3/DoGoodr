package com.vidalia.backend.service;

import com.vidalia.backend.dto.label.AssignedLabelDTO;
import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.model.matchmaking.Label;
import com.vidalia.backend.model.matchmaking.OpportunityLabelLink;
import com.vidalia.backend.model.matchmaking.OrganisationLabelLink;
import com.vidalia.backend.model.matchmaking.VolunteerLabelLink;
import com.vidalia.backend.repository.OpportunityRepository;
import com.vidalia.backend.repository.OrganisationProfileRepository;
import com.vidalia.backend.repository.VolunteerProfileRepository;
import com.vidalia.backend.repository.matchmaking.LabelRepository;
import com.vidalia.backend.repository.matchmaking.OpportunityLabelLinkRepository;
import com.vidalia.backend.repository.matchmaking.OrganisationLabelLinkRepository;
import com.vidalia.backend.repository.matchmaking.VolunteerLabelLinkRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class LabelAssignmentService {

    private final VolunteerLabelLinkRepository volunteerLabelLinkRepository;
    private final OpportunityLabelLinkRepository opportunityLabelLinkRepository;
    private final OrganisationLabelLinkRepository organisationLabelLinkRepository;
    private final LabelRepository labelRepository;

    private final VolunteerProfileRepository volunteerRepository;
    private final OrganisationProfileRepository organisationRepository;
    private final OpportunityRepository opportunityRepository;

    @Transactional
    public void setVolunteerLabels(List<AssignedLabelDTO> labels, UUID volunteerId) {
        volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + volunteerId));

        Map<Long, Label> labelsById = validateLabelList(labels);

        volunteerLabelLinkRepository.deleteAllByVolunteerId(volunteerId);

        List<VolunteerLabelLink> links = new ArrayList<>();
        for (AssignedLabelDTO assignedLabelDTO : labels) {
            Label label = labelsById.get(assignedLabelDTO.getLabelId());
            links.add(new VolunteerLabelLink(volunteerId, label, assignedLabelDTO.getWeight()));
        }

        volunteerLabelLinkRepository.saveAll(links);
    }

    @Transactional(readOnly = true)
    public List<AssignedLabelDTO> getVolunteerLabels(UUID volunteerId) {
        volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + volunteerId));

        List<VolunteerLabelLink> links = volunteerLabelLinkRepository.findAllByVolunteerId(volunteerId);
        List<AssignedLabelDTO> dtos = new ArrayList<>();
        for (VolunteerLabelLink link : links) {
            dtos.add(new AssignedLabelDTO(link.getLabel().getId(), link.getWeight()));
        }
        return dtos;
    }

    @Transactional
    public void setOrganisationLabels(List<AssignedLabelDTO> labels, UUID organisationId) {
        organisationRepository.findById(organisationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation not found with id: " + organisationId));

        Map<Long, Label> labelsById = validateLabelList(labels);

        organisationLabelLinkRepository.deleteAllByOrganisationId(organisationId);

        List<OrganisationLabelLink> links = new ArrayList<>();
        for (AssignedLabelDTO assignedLabelDTO : labels) {
            Label label = labelsById.get(assignedLabelDTO.getLabelId());
            links.add(new OrganisationLabelLink(organisationId, label, assignedLabelDTO.getWeight()));
        }

        organisationLabelLinkRepository.saveAll(links);
    }

    @Transactional(readOnly = true)
    public List<AssignedLabelDTO> getOrganisationLabels(UUID organisationId) {
        organisationRepository.findById(organisationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation not found with id: " + organisationId));

        List<OrganisationLabelLink> links = organisationLabelLinkRepository.findAllByOrganisationId(organisationId);
        List<AssignedLabelDTO> dtos = new ArrayList<>();
        for (OrganisationLabelLink link : links) {
            dtos.add(new AssignedLabelDTO(link.getLabel().getId(), link.getWeight()));
        }
        return dtos;
    }

    @Transactional
    public void setOpportunityLabels(List<AssignedLabelDTO> labels, UUID opportunityId) {
        opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + opportunityId));

        Map<Long, Label> labelsById = validateLabelList(labels);

        opportunityLabelLinkRepository.deleteAllByOpportunityId(opportunityId);

        List<OpportunityLabelLink> links = new ArrayList<>();
        for (AssignedLabelDTO assignedLabelDTO : labels) {
            Label label = labelsById.get(assignedLabelDTO.getLabelId());
            links.add(new OpportunityLabelLink(opportunityId, label, assignedLabelDTO.getWeight()));
        }

        opportunityLabelLinkRepository.saveAll(links);

    }

    @Transactional(readOnly = true)
    public List<AssignedLabelDTO> getOpportunityLabels(UUID opportunityId) {
        opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + opportunityId));

        List<OpportunityLabelLink> links = opportunityLabelLinkRepository.findAllByOpportunityId(opportunityId);
        List<AssignedLabelDTO> dtos = new ArrayList<>();
        for (OpportunityLabelLink link : links) {
            dtos.add(new AssignedLabelDTO(link.getLabel().getId(), link.getWeight()));
        }
        return dtos;

    }


    // Checks the list of labels for duplicates and existence, then returns a map of label id to label entity for all valid labels
    private Map<Long, Label> validateLabelList(List<AssignedLabelDTO> labels) {
        Set<Long> labelIds = new HashSet<>();
        for (AssignedLabelDTO assignedLabelDTO : labels) {
            if (!labelIds.add(assignedLabelDTO.getLabelId())) {
                throw new IllegalArgumentException("Duplicate label id in request: " + assignedLabelDTO.getLabelId());
            }
        }

        Map<Long, Label> labelsById = new HashMap<>();
        for (Label label : labelRepository.findAllById(labelIds)) {
            labelsById.put(label.getId(), label);
        }

        if (labelsById.size() != labelIds.size()) {
            for (Long labelId : labelIds) {
                if (!labelsById.containsKey(labelId)) {
                    throw new ResourceNotFoundException("Label not found with id: " + labelId);
                }
            }
        }
        return labelsById;
    }

}

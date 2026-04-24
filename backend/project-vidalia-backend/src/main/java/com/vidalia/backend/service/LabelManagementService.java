package com.vidalia.backend.service;

import com.vidalia.backend.dto.label.CreateLabelDTO;
import com.vidalia.backend.mapper.LabelMapper;
import com.vidalia.backend.model.matchmaking.Label;
import com.vidalia.backend.repository.OpportunityRepository;
import com.vidalia.backend.repository.OrganisationProfileRepository;
import com.vidalia.backend.repository.VolunteerProfileRepository;
import com.vidalia.backend.repository.matchmaking.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class LabelManagementService {

    private final VolunteerLabelLinkRepository volunteerLabelLinkRepository;
    private final OpportunityLabelLinkRepository opportunityLabelLinkRepository;
    private final OrganisationLabelLinkRepository organisationLabelLinkRepository;
    private final LabelRepository labelRepository;
    private final SemanticTagRepository semanticTagRepository;

    private final LabelMapper labelMapper;

    private final VolunteerProfileRepository volunteerRepository;
    private final OrganisationProfileRepository organisationRepository;
    private final OpportunityRepository opportunityRepository;

    public void createNewLabel(CreateLabelDTO dto) {
        //Check that name is unique
        if (labelRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Label with name '" + dto.getName() + "' already exists.");
        }

        //Check that Semantic Tag is valid if provided
        if (dto.getSemanticTag() != null && !semanticTagRepository.existsById(dto.getSemanticTag().getId())) {
            throw new IllegalArgumentException("Semantic Tag with id '" + dto.getSemanticTag().getId() + "' does not exist.");
        }

        Label label = labelMapper.toEntity(dto);
        labelRepository.save(label);
    }

}

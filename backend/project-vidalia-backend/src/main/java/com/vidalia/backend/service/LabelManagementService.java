package com.vidalia.backend.service;

import com.vidalia.backend.dto.label.CreateLabelDTO;
import com.vidalia.backend.dto.label.LabelDTO;
import com.vidalia.backend.mapper.LabelMapper;
import com.vidalia.backend.model.matchmaking.Label;
import com.vidalia.backend.model.matchmaking.LabelType;
import com.vidalia.backend.repository.OpportunityRepository;
import com.vidalia.backend.repository.OrganisationProfileRepository;
import com.vidalia.backend.repository.VolunteerProfileRepository;
import com.vidalia.backend.repository.matchmaking.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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


    public List<LabelDTO> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toDTO)
                .toList();
    }

    public List<LabelDTO> getLabelsByType(LabelType type) {
        return labelRepository.findAllByType(type).stream()
                .map(labelMapper::toDTO)
                .toList();
    }

    public LabelDTO getLabelById(Long id) {
        return labelRepository.findById(id)
                .map(labelMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Label not found with id: " + id));
    }

    public LabelDTO getLabelByName(String name) {
        return labelRepository.findByNameIgnoreCase(name)
                .map(labelMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Label not found with name: " + name));
    }

    public List<LabelDTO> getLabelsBySemanticTagId(Long semanticTagId) {
        return labelRepository.findAllBySemanticTagId(semanticTagId).stream()
                .map(labelMapper::toDTO)
                .toList();
    }

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

    public LabelDTO updateLabel(Long id, CreateLabelDTO dto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Label not found with id: " + id));

        //Check that name is unique if changed
        if (dto.getName() != null && !dto.getName().equalsIgnoreCase(label.getName()) && labelRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Label with name '" + dto.getName() + "' already exists.");
        }

        //Check that Semantic Tag is valid if provided
        if (dto.getSemanticTag() != null && !semanticTagRepository.existsById(dto.getSemanticTag().getId())) {
            throw new IllegalArgumentException("Semantic Tag with id '" + dto.getSemanticTag().getId() + "' does not exist.");
        }

        labelMapper.updateEntity(label, dto);
        labelRepository.save(label);
        return labelMapper.toDTO(label);
    }

    public void deleteLabel(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new IllegalArgumentException("Label not found with id: " + id);
        }

        //Delete all label links for this label to avoid foreign key constraint violations
        volunteerLabelLinkRepository.deleteAllByLabelId(id);
        opportunityLabelLinkRepository.deleteAllByLabelId(id);
        organisationLabelLinkRepository.deleteAllByLabelId(id);

        labelRepository.deleteById(id);
    }

}

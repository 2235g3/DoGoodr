package com.vidalia.backend.service;

import com.vidalia.backend.dto.semanticTag.SemanticTagDTO;
import com.vidalia.backend.mapper.LabelMapper;
import com.vidalia.backend.model.matchmaking.Label;
import com.vidalia.backend.model.matchmaking.SemanticTag;
import com.vidalia.backend.repository.matchmaking.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class SemanticTagManagementService {

    private final LabelRepository labelRepository;
    private final SemanticTagRepository semanticTagRepository;

    @Transactional(readOnly = true)
    public List<SemanticTagDTO> getAllSemanticTags() {
        return semanticTagRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SemanticTagDTO getSemanticTagById(Long id) {
        return semanticTagRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Semantic tag not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public SemanticTagDTO getSemanticTagByName(String name) {
        return semanticTagRepository.findByNameIgnoreCase(name)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Semantic tag not found with name: " + name));
    }

    @Transactional
    public SemanticTagDTO createNewSemanticTag(String name) {
        if (semanticTagRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new IllegalArgumentException("Semantic tag with name already exists: " + name);
        }
        SemanticTag tag = new SemanticTag();
        tag.setName(name);
        return toDTO(semanticTagRepository.save(tag));
    }

    @Transactional
    public SemanticTagDTO updateSemanticTag(Long id, String newName) {
        SemanticTag tag = semanticTagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Semantic tag not found with id: " + id));
        if (semanticTagRepository.findByNameIgnoreCase(newName).isPresent()) {
            throw new IllegalArgumentException("Semantic tag with name already exists: " + newName);
        }
        tag.setName(newName);
        return toDTO(semanticTagRepository.save(tag));
    }

    @Transactional
    public void deleteSemanticTagById(Long id) {
        SemanticTag tag = semanticTagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Semantic tag not found with id: " + id));
        // Unlink all labels associated with this semantic tag
        List<Label> linkedLabels = labelRepository.findAllBySemanticTagId(id);
        for (Label label : linkedLabels) {
            label.setSemanticTag(null);
            labelRepository.save(label);
        }
        semanticTagRepository.delete(tag);
    }

    private SemanticTagDTO toDTO(SemanticTag tag) {
        SemanticTagDTO dto = new SemanticTagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }
}

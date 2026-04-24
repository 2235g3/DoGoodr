package com.vidalia.backend.service;

import com.vidalia.backend.dto.semanticTag.SemanticLinkDTO;
import com.vidalia.backend.dto.semanticTag.SemanticTagDTO;
import com.vidalia.backend.model.matchmaking.Label;
import com.vidalia.backend.model.matchmaking.SemanticLink;
import com.vidalia.backend.model.matchmaking.SemanticTag;
import com.vidalia.backend.repository.matchmaking.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class SemanticTagManagementService {

    private final LabelRepository labelRepository;
    private final SemanticTagRepository semanticTagRepository;
    private final SemanticLinkRepository semanticLinkRepository;

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
        String normalizedName = normalizeTagName(name, "Semantic tag name cannot be blank");
        return semanticTagRepository.findByNameIgnoreCase(normalizedName)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Semantic tag not found with name: " + normalizedName));
    }

    @Transactional
    public SemanticTagDTO createNewSemanticTag(String name) {
        String normalizedName = normalizeTagName(name, "Semantic tag name cannot be blank");
        if (semanticTagRepository.findByNameIgnoreCase(normalizedName).isPresent()) {
            throw new IllegalArgumentException("Semantic tag with name already exists: " + normalizedName);
        }
        SemanticTag tag = new SemanticTag();
        tag.setName(normalizedName);
        return toDTO(semanticTagRepository.save(tag));
    }

    @Transactional
    public SemanticTagDTO updateSemanticTag(Long id, String newName) {
        SemanticTag tag = semanticTagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Semantic tag not found with id: " + id));

        String normalizedName = normalizeTagName(newName, "Semantic tag name cannot be blank");
        Optional<SemanticTag> byName = semanticTagRepository.findByNameIgnoreCase(normalizedName);
        if (byName.isPresent() && !byName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Semantic tag with name already exists: " + normalizedName);
        }

        tag.setName(normalizedName);
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
        }
        labelRepository.saveAll(linkedLabels);

        // Delete all semantic links involving this tag
        semanticLinkRepository.deleteAllByTagOne_Id(id);
        semanticLinkRepository.deleteAllByTagTwo_Id(id);

        semanticTagRepository.delete(tag);
    }

    public void setSemanticRelationship(SemanticLinkDTO link) {
        String oneName = normalizeTagName(link.getOne().getName(), "First semantic tag name cannot be blank");
        String twoName = normalizeTagName(link.getTwo().getName(), "Second semantic tag name cannot be blank");

        // Reject self-links
        if (oneName.equalsIgnoreCase(twoName)) {
            throw new IllegalArgumentException("Cannot link a semantic tag to itself: " + oneName);
        }
        // Validate weight range (0...1)
        if (link.getWeight() < 0 || link.getWeight() > 1) {
            throw new IllegalArgumentException("Weight must be between 0 and 1: " + link.getWeight());
        }

        // Validate that both tags exist
        SemanticTag one = semanticTagRepository.findByNameIgnoreCase(oneName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Semantic tag not found with name: " + oneName));

        SemanticTag two = semanticTagRepository.findByNameIgnoreCase(twoName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Semantic tag not found with name: " + twoName));

        long smallerId = Math.min(one.getId(), two.getId());
        long largerId = Math.max(one.getId(), two.getId());

        // Check if link already exists
        Optional<SemanticLink> existingLink = semanticLinkRepository.findByTagOne_IdAndTagTwo_Id(smallerId, largerId);
        if (existingLink.isPresent()) {
            SemanticLink linkEntity = existingLink.get();
            linkEntity.setWeight(link.getWeight());
            semanticLinkRepository.save(linkEntity);
        } else {
            // Always set the tag with smaller id as tagOne to avoid duplicate links in opposite direction
            SemanticLink linkEntity = new SemanticLink(
                    one.getId() < two.getId() ? one : two,
                    one.getId() < two.getId() ? two : one,
                    link.getWeight()
            );
            semanticLinkRepository.save(linkEntity);
        }

    }

    private SemanticTagDTO toDTO(SemanticTag tag) {
        SemanticTagDTO dto = new SemanticTagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }

    private String normalizeTagName(String rawName, String errorMessage) {
        if (rawName == null || rawName.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return rawName.trim().toLowerCase();
    }
}

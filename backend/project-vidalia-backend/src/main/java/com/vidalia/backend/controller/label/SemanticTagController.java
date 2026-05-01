package com.vidalia.backend.controller.label;

import com.vidalia.backend.dto.semanticTag.SemanticLinkDTO;
import com.vidalia.backend.dto.semanticTag.SemanticTagDTO;
import com.vidalia.backend.service.SemanticTagManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/semantic-tags")
public class SemanticTagController {

    private final SemanticTagManagementService semanticTagManagementService;

    public SemanticTagController(SemanticTagManagementService semanticTagManagementService) {
        this.semanticTagManagementService = semanticTagManagementService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER', 'ORGANISATION')")
    public ResponseEntity<List<SemanticTagDTO>> getSemanticTags() {
        return ResponseEntity.ok(semanticTagManagementService.getAllSemanticTags());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER', 'ORGANISATION')")
    public ResponseEntity<SemanticTagDTO> getSemanticTag(@PathVariable Long id) {
        return ResponseEntity.ok(semanticTagManagementService.getSemanticTagById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SemanticTagDTO> createSemanticTag(@Valid @RequestBody SemanticTagDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(semanticTagManagementService.createNewSemanticTag(dto.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SemanticTagDTO> updateSemanticTag(@PathVariable Long id,
                                                            @Valid @RequestBody SemanticTagDTO dto) {
        return ResponseEntity.ok(semanticTagManagementService.updateSemanticTag(id, dto.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSemanticTag(@PathVariable Long id) {
        semanticTagManagementService.deleteSemanticTagById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/relationships")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setSemanticRelationship(@Valid @RequestBody SemanticLinkDTO dto) {
        semanticTagManagementService.setSemanticRelationship(dto);
        return ResponseEntity.noContent().build();
    }
}

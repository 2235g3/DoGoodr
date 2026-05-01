package com.vidalia.backend.controller.label;

import com.vidalia.backend.dto.label.AssignedLabelDTO;
import com.vidalia.backend.dto.label.CreateLabelDTO;
import com.vidalia.backend.dto.label.LabelDTO;
import com.vidalia.backend.dto.profile.VProfileResponseDTO;
import com.vidalia.backend.model.matchmaking.LabelType;
import com.vidalia.backend.security.CustomUserDetails;
import com.vidalia.backend.service.LabelAssignmentService;
import com.vidalia.backend.service.LabelManagementService;
import com.vidalia.backend.service.VolunteerProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    private final LabelManagementService labelManagementService;
    private final LabelAssignmentService labelAssignmentService;
    private final VolunteerProfileService volunteerProfileService;

    public LabelController(LabelManagementService labelManagementService,
                           LabelAssignmentService labelAssignmentService,
                           VolunteerProfileService volunteerProfileService) {
        this.labelManagementService = labelManagementService;
        this.labelAssignmentService = labelAssignmentService;
        this.volunteerProfileService = volunteerProfileService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER', 'ORGANISATION')")
    public ResponseEntity<List<LabelDTO>> getLabels(@RequestParam(required = false) LabelType type) {
        if (type != null) {
            return ResponseEntity.ok(labelManagementService.getLabelsByType(type));
        }
        return ResponseEntity.ok(labelManagementService.getAllLabels());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createLabel(@Valid @RequestBody CreateLabelDTO dto) {
        labelManagementService.createNewLabel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LabelDTO> updateLabel(@PathVariable Long id,
                                                @Valid @RequestBody CreateLabelDTO dto) {
        return ResponseEntity.ok(labelManagementService.updateLabel(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        labelManagementService.deleteLabel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/volunteer/me")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<List<AssignedLabelDTO>> getMyVolunteerLabels(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID volunteerId = getCurrentVolunteerProfileId(userDetails);
        return ResponseEntity.ok(labelAssignmentService.getVolunteerLabels(volunteerId));
    }

    @PutMapping("/volunteer/me")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<List<AssignedLabelDTO>> setMyVolunteerLabels(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody List<AssignedLabelDTO> labels) {
        UUID volunteerId = getCurrentVolunteerProfileId(userDetails);
        labelAssignmentService.setVolunteerLabels(labels, volunteerId);
        return ResponseEntity.ok(labelAssignmentService.getVolunteerLabels(volunteerId));
    }

    private UUID getCurrentVolunteerProfileId(CustomUserDetails userDetails) {
        VProfileResponseDTO profile = volunteerProfileService.getVolunteerProfileByUserId(userDetails.getId());
        return profile.getId();
    }
}

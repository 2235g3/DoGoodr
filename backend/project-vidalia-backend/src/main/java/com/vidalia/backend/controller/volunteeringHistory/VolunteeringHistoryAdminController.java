package com.vidalia.backend.controller.volunteeringHistory;

import com.vidalia.backend.dto.volunteerHistory.VolunteerHistoryResponseDTO;
import com.vidalia.backend.service.VolunteerHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/volunteering-history")
@PreAuthorize("hasRole('ADMIN')")
public class VolunteeringHistoryAdminController {

    private final VolunteerHistoryService volunteerHistoryService;

    public VolunteeringHistoryAdminController(VolunteerHistoryService volunteerHistoryService) {
        this.volunteerHistoryService = volunteerHistoryService;
    }

    // Admin: get volunteer history for a specific volunteer
    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<List<VolunteerHistoryResponseDTO>> getVolunteerHistoryByVolunteerId(@PathVariable UUID volunteerId) {
        return ResponseEntity.ok(volunteerHistoryService.getVolunteerHistoryForVolunteer(volunteerId));
    }

    // Admin: get volunteer history for a specific opportunity and organisation
    @GetMapping("/opportunity/{opportunityId}/organisation/{organisationId}")
    public ResponseEntity<List<VolunteerHistoryResponseDTO>> getVolunteerHistoryByOpportunityAndOrganisation(
            @PathVariable UUID opportunityId,
            @PathVariable UUID organisationId) {
        return ResponseEntity.ok(volunteerHistoryService.getVolunteerHistoryForOpportunityAndOrganisation(opportunityId, organisationId));
    }
}


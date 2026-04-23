package com.vidalia.backend.controller.opportunity;

import com.vidalia.backend.dto.opportunity.CreateOpportunityDTO;
import com.vidalia.backend.dto.opportunity.OpportunityResponseDTO;
import com.vidalia.backend.dto.opportunity.UpdateOpportunityDTO;
import com.vidalia.backend.dto.profile.OProfileResponseDTO;
import com.vidalia.backend.model.Role;
import com.vidalia.backend.security.CustomUserDetails;
import com.vidalia.backend.service.OpportunityService;
import com.vidalia.backend.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final ProfileService profileService;

    public OpportunityController(OpportunityService opportunityService, ProfileService profileService) {
        this.opportunityService = opportunityService;
        this.profileService = profileService;
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OpportunityResponseDTO>> getAllOpportunities() {
        return ResponseEntity.ok(opportunityService.getAllOpportunities());
    }

    @GetMapping("/organisation/{organisationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER', 'ORGANISATION')")
    public ResponseEntity<List<OpportunityResponseDTO>> getOpportunitiesByOrganisation(@PathVariable UUID organisationId) {
        return ResponseEntity.ok(opportunityService.getAllOpportunitiesByOrganisation(organisationId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER', 'ORGANISATION')")
    public ResponseEntity<OpportunityResponseDTO> getOpportunityById(@PathVariable UUID id,
                                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails.getRole() == Role.ORGANISATION) {
            UUID organisationId = getMyOrganisationId(userDetails);
            return ResponseEntity.ok(opportunityService.getOpportunityByIdForOrganisation(id, organisationId));
        }
        return ResponseEntity.ok(opportunityService.getOpportunityById(id));
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ORGANISATION')")
    public ResponseEntity<OpportunityResponseDTO> createOpportunity(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                    @Valid @RequestBody CreateOpportunityDTO createOpportunityDTO) {
        UUID organisationId = getMyOrganisationId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(opportunityService.createOpportunity(createOpportunityDTO, organisationId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISATION')")
    public ResponseEntity<OpportunityResponseDTO> updateOpportunity(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                    @PathVariable UUID id,
                                                                    @Valid @RequestBody UpdateOpportunityDTO updateOpportunityDTO) {
        UUID organisationId = getMyOrganisationId(userDetails);
        return ResponseEntity.ok(opportunityService.updateOpportunityForOrganisation(id, updateOpportunityDTO, organisationId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISATION')")
    public ResponseEntity<Void> deleteOpportunity(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable UUID id) {
        UUID organisationId = getMyOrganisationId(userDetails);
        opportunityService.deleteOpportunityForOrganisation(id, organisationId);
        return ResponseEntity.noContent().build();
    }

    private UUID getMyOrganisationId(CustomUserDetails userDetails) {
        OProfileResponseDTO profile = profileService.getOrganisationProfileByUserId(userDetails.getId());
        return profile.getId();
    }
}

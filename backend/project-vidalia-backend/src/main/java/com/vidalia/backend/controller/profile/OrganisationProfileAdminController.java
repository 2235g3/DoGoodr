package com.vidalia.backend.controller.profile;

import com.vidalia.backend.dto.profile.OProfileResponseDTO;
import com.vidalia.backend.service.OrganisationProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/organisation-profile")
@PreAuthorize("hasRole('ADMIN')")
public class OrganisationProfileAdminController {

    private final OrganisationProfileService organisationProfileService;

    public OrganisationProfileAdminController(OrganisationProfileService organisationProfileService) {
        this.organisationProfileService = organisationProfileService;
    }

    @GetMapping("/")
    public ResponseEntity<List<OProfileResponseDTO>> getAllOrganisationProfiles() {
        return ResponseEntity.ok(organisationProfileService.getAllOrganisationProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OProfileResponseDTO> getOrganisationProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(organisationProfileService.getOrganisationProfileById(id));
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<OProfileResponseDTO> verifyOrganisationProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(organisationProfileService.verifyOrganisationProfile(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganisationProfile(@PathVariable UUID id) {
        organisationProfileService.deleteOrganisationProfile(id);
        return ResponseEntity.noContent().build();
    }
}


package com.vidalia.backend.controller;

import com.vidalia.backend.dto.profile.OProfileResponseDTO;
import com.vidalia.backend.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/organisation-profile")
@PreAuthorize("hasRole('ADMIN')")
public class OrganisationProfileAdminController {

    private final ProfileService profileService;

    public OrganisationProfileAdminController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/")
    public ResponseEntity<List<OProfileResponseDTO>> getAllOrganisationProfiles() {
        return ResponseEntity.ok(profileService.getAllOrganisationProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OProfileResponseDTO> getOrganisationProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(profileService.getOrganisationProfileById(id));
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<OProfileResponseDTO> verifyOrganisationProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(profileService.verifyOrganisationProfile(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganisationProfile(@PathVariable UUID id) {
        profileService.deleteOrganisationProfile(id);
        return ResponseEntity.noContent().build();
    }
}


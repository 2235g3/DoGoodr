package com.vidalia.backend.controller.profile;

import com.vidalia.backend.dto.profile.OProfileResponseDTO;
import com.vidalia.backend.dto.profile.UpdateOrganisationProfileDTO;
import com.vidalia.backend.security.CustomUserDetails;
import com.vidalia.backend.service.OrganisationProfileService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/organisation-profile")
public class OrganisationProfileController {

    private final OrganisationProfileService organisationProfileService;

    public OrganisationProfileController(OrganisationProfileService organisationProfileService) {
        this.organisationProfileService = organisationProfileService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ORGANISATION')")
    public ResponseEntity<OProfileResponseDTO> getMyOrganisationProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(organisationProfileService.getOrganisationProfileByUserId(userId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER', 'ORGANISATION')")
    public ResponseEntity<List<OProfileResponseDTO>> getOrganisationProfiles() {
        return ResponseEntity.ok(organisationProfileService.getAllOrganisationProfiles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER', 'ORGANISATION')")
    public ResponseEntity<OProfileResponseDTO> getOrganisationProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(organisationProfileService.getOrganisationProfileById(id));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('ORGANISATION')")
    public ResponseEntity<OProfileResponseDTO> updateMyOrganisationProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                           @Valid @RequestBody UpdateOrganisationProfileDTO updateDTO) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(organisationProfileService.updateOrganisationProfile(updateDTO, userId));
    }

    @PutMapping(value = "/me/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ORGANISATION')")
    public ResponseEntity<OProfileResponseDTO> uploadMyOrganisationProfilePicture(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(organisationProfileService.uploadOrganisationProfilePicture(userId, file));
    }

    @DeleteMapping("/me/profile-picture")
    @PreAuthorize("hasRole('ORGANISATION')")
    public ResponseEntity<OProfileResponseDTO> deleteMyOrganisationProfilePicture(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(organisationProfileService.deleteOrganisationProfilePicture(userId));
    }
}

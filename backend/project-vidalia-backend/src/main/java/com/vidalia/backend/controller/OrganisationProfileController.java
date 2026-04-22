package com.vidalia.backend.controller;

import com.vidalia.backend.dto.profile.OProfileResponseDTO;
import com.vidalia.backend.dto.profile.UpdateOrganisationProfileDTO;
import com.vidalia.backend.security.CustomUserDetails;
import com.vidalia.backend.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/organisation-profile")
@PreAuthorize("hasRole('ORGANISATION')")
public class OrganisationProfileController {

    private final ProfileService profileService;

    public OrganisationProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<OProfileResponseDTO> getMyOrganisationProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(profileService.getOrganisationProfileByUserId(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<OProfileResponseDTO> updateMyOrganisationProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                           @Valid @RequestBody UpdateOrganisationProfileDTO updateDTO) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(profileService.updateOrganisationProfile(updateDTO, userId));
    }

    @PutMapping(value = "/me/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OProfileResponseDTO> uploadMyOrganisationProfilePicture(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(profileService.uploadOrganisationProfilePicture(userId, file));
    }

    @DeleteMapping("/me/profile-picture")
    public ResponseEntity<OProfileResponseDTO> deleteMyOrganisationProfilePicture(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(profileService.deleteOrganisationProfilePicture(userId));
    }
}

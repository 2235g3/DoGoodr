package com.vidalia.backend.controller;

import com.vidalia.backend.dto.profile.CreateOrganisationProfileDTO;
import com.vidalia.backend.dto.profile.OProfileResponseDTO;
import com.vidalia.backend.dto.profile.UpdateOrganisationProfileDTO;
import com.vidalia.backend.security.CustomUserDetails;
import com.vidalia.backend.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
}


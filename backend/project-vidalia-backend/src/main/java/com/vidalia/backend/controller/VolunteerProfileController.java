package com.vidalia.backend.controller;

import com.vidalia.backend.dto.profile.CreateVolunteerProfileDTO;
import com.vidalia.backend.dto.profile.UpdateVolunteerProfileDTO;
import com.vidalia.backend.dto.profile.VProfileResponseDTO;
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
@RequestMapping("/api/volunteer-profile")
@PreAuthorize("hasRole('VOLUNTEER')")
public class VolunteerProfileController {

    private final ProfileService profileService;

    public VolunteerProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<VProfileResponseDTO> getMyVolunteerProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(profileService.getVolunteerProfileByUserId(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<VProfileResponseDTO> updateMyVolunteerProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                        @Valid @RequestBody UpdateVolunteerProfileDTO updateDTO) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(profileService.updateVolunteerProfile(updateDTO, userId));
    }
}


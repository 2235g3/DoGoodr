package com.vidalia.backend.controller.profile;

import com.vidalia.backend.dto.profile.UpdateVolunteerProfileDTO;
import com.vidalia.backend.dto.profile.VProfileResponseDTO;
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

    @PutMapping(value = "/me/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VProfileResponseDTO> uploadMyVolunteerProfilePicture(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(profileService.uploadVolunteerProfilePicture(userId, file));
    }

    @PutMapping(value = "/me/cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VProfileResponseDTO> uploadMyVolunteerCV(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(profileService.uploadVolunteerCV(userId, file));
    }

    @DeleteMapping("/me/profile-picture")
    public ResponseEntity<VProfileResponseDTO> deleteMyVolunteerProfilePicture(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(profileService.deleteVolunteerProfilePicture(userId));
    }

    @DeleteMapping("/me/cv")
    public ResponseEntity<VProfileResponseDTO> deleteMyVolunteerCV(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        return ResponseEntity.ok(profileService.deleteVolunteerCV(userId));
    }
}

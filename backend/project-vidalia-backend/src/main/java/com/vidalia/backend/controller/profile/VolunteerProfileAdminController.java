package com.vidalia.backend.controller.profile;

import com.vidalia.backend.dto.profile.VProfileResponseDTO;
import com.vidalia.backend.service.VolunteerProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/volunteer-profile")
@PreAuthorize("hasRole('ADMIN')")
public class VolunteerProfileAdminController {

    private final VolunteerProfileService profileService;

    public VolunteerProfileAdminController(VolunteerProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/")
    public ResponseEntity<List<VProfileResponseDTO>> getAllVolunteerProfiles() {
        return ResponseEntity.ok(profileService.getAllVolunteerProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VProfileResponseDTO> getVolunteerProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(profileService.getVolunteerProfileById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVolunteerProfile(@PathVariable UUID id) {
        profileService.deleteVolunteerProfile(id);
        return ResponseEntity.noContent().build();
    }
}


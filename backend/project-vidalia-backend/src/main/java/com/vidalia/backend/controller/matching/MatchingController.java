package com.vidalia.backend.controller.matching;

import com.vidalia.backend.dto.profile.VProfileResponseDTO;
import com.vidalia.backend.matching.dto.MatchedOpportunityDTO;
import com.vidalia.backend.matching.service.MatchingService;
import com.vidalia.backend.security.CustomUserDetails;
import com.vidalia.backend.service.VolunteerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matching/volunteer")
public class MatchingController {

    MatchingService matchingService;
    VolunteerProfileService volunteerProfileService;

    @GetMapping
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<List<MatchedOpportunityDTO>> getMatchedOpportunitiesForVolunteer(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Get the volunteer's profile using their user ID
        // Throws an exception if the volunteer profile is not found, which will be handled by the global exception handler
        UUID userId = userDetails.getId();
        VProfileResponseDTO profile = volunteerProfileService.getVolunteerProfileByUserId(userId);

        // Call the matching service to get matched opportunities for the volunteer
        List<MatchedOpportunityDTO> matchedOpportunities = matchingService.getMatchesForVolunteer(profile.getId());
        return ResponseEntity.ok(matchedOpportunities);
    }
}

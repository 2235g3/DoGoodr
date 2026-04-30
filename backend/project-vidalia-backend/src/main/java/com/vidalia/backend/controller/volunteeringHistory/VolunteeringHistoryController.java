package com.vidalia.backend.controller.volunteeringHistory;

import com.vidalia.backend.dto.volunteerHistory.*;
import com.vidalia.backend.dto.volunteerHistory.VolunteerHistoryResponseDTO;
import com.vidalia.backend.security.CustomUserDetails;
import com.vidalia.backend.service.OrganisationProfileService;
import com.vidalia.backend.service.VolunteerHistoryService;
import com.vidalia.backend.service.VolunteerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/volunteering-history")
@PreAuthorize("hasAnyRole('VOLUNTEER','ORGANISATION')")
@RequiredArgsConstructor
public class VolunteeringHistoryController {

	private final VolunteerHistoryService volunteerHistoryService;
	private final VolunteerProfileService volunteerProfileService;
	private final OrganisationProfileService organisationProfileService;

	// VOLUNTEER endpoints
	@GetMapping("/me")
	@PreAuthorize("hasRole('VOLUNTEER')")
	public ResponseEntity<List<VolunteerHistoryResponseDTO>> getMyVolunteerHistory(
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		UUID userId = userDetails.getId();
		UUID volunteerProfileId = getMyVolunteerId(userId);
		return ResponseEntity.ok(volunteerHistoryService.getVolunteerHistoryForVolunteer(volunteerProfileId));
	}

	// ORGANISATION endpoints
	@GetMapping("/opportunity/{opportunityId}")
	@PreAuthorize("hasRole('ORGANISATION')")
	public ResponseEntity<List<VolunteerHistoryResponseDTO>> getVolunteerHistoryForOpportunity(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable UUID opportunityId) {
		UUID organisationId = getMyOrganisationId(userDetails);
		return ResponseEntity.ok(volunteerHistoryService.getVolunteerHistoryForOpportunityAndOrganisation(opportunityId, organisationId));
	}

	@PostMapping("/volunteer/{volunteerId}")
	@PreAuthorize("hasRole('ORGANISATION')")
	public ResponseEntity<VolunteerHistoryResponseDTO> createVolunteerHistoryEntry(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable UUID volunteerId,
			@Valid @RequestBody CreateVolunteerHistoryDTO createDTO) {
		UUID organisationId = getMyOrganisationId(userDetails);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(volunteerHistoryService.createVolunteerHistoryEntry(createDTO, organisationId, volunteerId));
	}

	@PutMapping("/{logId}/date-range")
	@PreAuthorize("hasRole('ORGANISATION')")
	public ResponseEntity<VolunteerHistoryResponseDTO> updateDateRange(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable long logId,
			@Valid @RequestBody UpdateVolunteerHistoryDateRangeDTO dto) {
		UUID organisationId = getMyOrganisationId(userDetails);
		return ResponseEntity.ok(volunteerHistoryService.updateDateRange(logId, organisationId, dto));
	}

	@PatchMapping("/{logId}/comment")
	@PreAuthorize("hasRole('ORGANISATION')")
	public ResponseEntity<VolunteerHistoryResponseDTO> setLoggedMessage(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable long logId,
			@Valid @RequestBody VolunteerHistoryCommentDTO commentDTO) {
		UUID organisationId = getMyOrganisationId(userDetails);
		return ResponseEntity.ok(volunteerHistoryService.setLoggedMessage(commentDTO, logId, organisationId));
	}

	@PatchMapping("/{logId}/hours")
	@PreAuthorize("hasRole('ORGANISATION')")
	public ResponseEntity<VolunteerHistoryResponseDTO> addVolunteeredHours(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable long logId,
			@Valid @RequestBody VolunteeredHoursDTO dto) {
		UUID organisationId = getMyOrganisationId(userDetails);
		return ResponseEntity.ok(volunteerHistoryService.addVolunteeredHours(dto, logId, organisationId));
	}

	private UUID getMyVolunteerId(UUID userId) {
		return volunteerProfileService.getVolunteerProfileByUserId(userId).getId();
	}

	private UUID getMyOrganisationId(CustomUserDetails userDetails) {
		return organisationProfileService.getOrganisationProfileByUserId(userDetails.getId()).getId();
	}
}

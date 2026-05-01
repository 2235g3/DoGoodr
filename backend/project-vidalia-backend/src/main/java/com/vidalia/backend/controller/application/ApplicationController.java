package com.vidalia.backend.controller.application;

import com.vidalia.backend.dto.application.ApplicationResponseDTO;
import com.vidalia.backend.dto.application.CreateApplicationDTO;
import com.vidalia.backend.dto.profile.OProfileResponseDTO;
import com.vidalia.backend.model.ApplicationStatus;
import com.vidalia.backend.security.CustomUserDetails;
import com.vidalia.backend.service.ApplicationService;
import com.vidalia.backend.service.OrganisationProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

	private final ApplicationService applicationService;
	private final OrganisationProfileService organisationProfileService;

	public ApplicationController(ApplicationService applicationService,
								 OrganisationProfileService organisationProfileService) {
		this.applicationService = applicationService;
		this.organisationProfileService = organisationProfileService;
	}

	// Admin endpoints (grouped under /admin)
	@GetMapping("/admin/")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ApplicationResponseDTO>> getAllApplications() {
		return ResponseEntity.ok(applicationService.getAllApplications());
	}

	@GetMapping("/admin/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApplicationResponseDTO> getApplicationById(@PathVariable UUID id) {
		return ResponseEntity.ok(applicationService.getApplicationById(id));
	}

	@GetMapping("/admin/opportunity/{opportunityId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsByOpportunity(
			@PathVariable UUID opportunityId) {
		return ResponseEntity.ok(applicationService.getAllApplicationsForOpportunity(opportunityId));
	}

	@GetMapping("/admin/volunteer/{volunteerId}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsByVolunteer(@PathVariable UUID volunteerId) {
		return ResponseEntity.ok(applicationService.getAllApplicationsForVolunteer(volunteerId));
	}

	// Volunteer endpoints
	@GetMapping("/me")
	@PreAuthorize("hasRole('VOLUNTEER')")
	public ResponseEntity<List<ApplicationResponseDTO>> getMyApplications(
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(applicationService.getAllApplicationsForVolunteerUser(userDetails.getId()));
	}

	@PostMapping("/me/{opportunityId}")
	@PreAuthorize("hasRole('VOLUNTEER')")
	public ResponseEntity<ApplicationResponseDTO> createApplication(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable UUID opportunityId,
			@Valid @RequestBody CreateApplicationDTO createApplicationDTO) {
		UUID userId = userDetails.getId();

		return ResponseEntity.status(HttpStatus.CREATED).body(
				applicationService.createApplicationForVolunteerUser(createApplicationDTO, userId, opportunityId));
	}

	@PutMapping("/me/{applicationId}/withdraw")
	@PreAuthorize("hasRole('VOLUNTEER')")
	public ResponseEntity<ApplicationResponseDTO> withdrawMyApplication(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable UUID applicationId) {
		return ResponseEntity.ok(applicationService.withdrawApplicationForVolunteerUser(
				userDetails.getId(), applicationId));
	}


	@GetMapping("/organisation/{opportunityId}")
	@PreAuthorize("hasRole('ORGANISATION')")
	public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsByOpportunityForOrganisation(
			@PathVariable UUID opportunityId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		UUID organisationId = getMyOrganisationId(userDetails);
		return ResponseEntity.ok(applicationService.getAllApplicationsForOpportunityByOrganisation(
				organisationId, opportunityId));
	}

	@GetMapping("/organisation")
	@PreAuthorize("hasRole('ORGANISATION')")
	public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsForOrganisation(
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		UUID organisationId = getMyOrganisationId(userDetails);
		return ResponseEntity.ok(applicationService.getAllApplicationsForOrganisation(organisationId));
	}

	@PutMapping("/{id}/status")
	@PreAuthorize("hasRole('ORGANISATION')")
	public ResponseEntity<ApplicationResponseDTO> updateApplicationStatus(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable UUID id,
			@RequestParam ApplicationStatus status) {
		UUID organisationId = getMyOrganisationId(userDetails);
		return ResponseEntity.ok(applicationService.updateApplicationStatus(organisationId, id, status));
	}

	private UUID getMyOrganisationId(CustomUserDetails userDetails) {
		OProfileResponseDTO profile = organisationProfileService.getOrganisationProfileByUserId(userDetails.getId());
		return profile.getId();
	}

}

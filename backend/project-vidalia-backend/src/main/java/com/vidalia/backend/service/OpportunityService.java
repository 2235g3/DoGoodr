package com.vidalia.backend.service;

import com.vidalia.backend.dto.opportunity.CreateOpportunityDTO;
import com.vidalia.backend.dto.opportunity.OpportunityResponseDTO;
import com.vidalia.backend.dto.opportunity.UpdateOpportunityDTO;
import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.mapper.OpportunityMapper;
import com.vidalia.backend.model.Opportunity;
import com.vidalia.backend.model.OpportunityStatus;
import com.vidalia.backend.model.OrganisationProfile;
import com.vidalia.backend.repository.OpportunityRepository;
import com.vidalia.backend.repository.OrganisationProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunityMapper opportunityMapper;
    private final OrganisationProfileRepository organisationRepository;

    @Transactional(readOnly = true)
    public List<OpportunityResponseDTO> getAllOpportunities() {
        return opportunityRepository.findAll().stream()
                .map(opportunityMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponseDTO> getOpenOpportunities() {
        return opportunityRepository.findAllByStatus(OpportunityStatus.OPEN).stream()
                .map(opportunityMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponseDTO> searchOpenOpportunities(String query,
                                                                Boolean remote,
                                                                UUID organisationId,
                                                                Integer maxHours,
                                                                LocalDate startsAfter,
                                                                LocalDate startsBefore,
                                                                Double latitude,
                                                                Double longitude,
                                                                Double maxDistanceKm,
                                                                String sort) {
        Stream<Opportunity> opportunities = opportunityRepository.findAllByStatus(OpportunityStatus.OPEN).stream();

        if (query != null && !query.isBlank()) {
            String normalizedQuery = query.trim().toLowerCase();
            opportunities = opportunities.filter(opportunity -> containsIgnoreCase(opportunity.getTitle(), normalizedQuery)
                    || containsIgnoreCase(opportunity.getDescription(), normalizedQuery)
                    || containsIgnoreCase(opportunity.getLocation(), normalizedQuery)
                    || containsIgnoreCase(opportunity.getOrganisationProfile().getDisplayName(), normalizedQuery));
        }

        if (remote != null) {
            opportunities = opportunities.filter(opportunity -> opportunity.isRemote() == remote);
        }

        if (organisationId != null) {
            opportunities = opportunities.filter(opportunity ->
                    opportunity.getOrganisationProfile().getId().equals(organisationId));
        }

        if (maxHours != null) {
            opportunities = opportunities.filter(opportunity ->
                    opportunity.getRequiredHours() == null || opportunity.getRequiredHours() <= maxHours);
        }

        if (startsAfter != null) {
            opportunities = opportunities.filter(opportunity -> !opportunity.getStartDate().isBefore(startsAfter));
        }

        if (startsBefore != null) {
            opportunities = opportunities.filter(opportunity -> !opportunity.getStartDate().isAfter(startsBefore));
        }

        if (latitude != null && longitude != null && maxDistanceKm != null) {
            opportunities = opportunities.filter(opportunity ->
                    opportunity.isRemote() || distanceKm(latitude, longitude, opportunity) <= maxDistanceKm);
        }

        Comparator<Opportunity> comparator = buildOpportunityComparator(sort, latitude, longitude);
        return opportunities
                .sorted(comparator)
                .map(opportunityMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OpportunityResponseDTO getOpportunityById(UUID id) {
        return opportunityRepository.findById(id)
                .map(opportunityMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponseDTO> getAllOpportunitiesByOrganisation(UUID organisationId) {
        return opportunityRepository.findAllByOrganisationProfileId(organisationId).stream()
                .map(opportunityMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OpportunityResponseDTO getOpportunityByIdForOrganisation(UUID opportunityId, UUID organisationId) {
        return opportunityRepository.findByIdAndOrganisationProfileId(opportunityId, organisationId)
                .map(opportunityMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Opportunity not found with id: " + opportunityId + " for organisation id: " + organisationId));
    }

    @Transactional
    public OpportunityResponseDTO createOpportunity(CreateOpportunityDTO dto, UUID profileId) {
        //Find owner profile
        OrganisationProfile owner = organisationRepository
                .findById(profileId).orElseThrow(() -> new ResourceNotFoundException("Organisation profile not found with id: " + profileId));

        Opportunity opportunity = opportunityMapper.toEntity(dto);
        opportunity.setOrganisationProfile(owner);
        return opportunityMapper.toDTO(opportunityRepository.save(opportunity));
    }

    @Transactional
    public OpportunityResponseDTO  updateOpportunity(UUID id, UpdateOpportunityDTO dto) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));

        opportunityMapper.updateEntity(opportunity, dto);
        return opportunityMapper.toDTO(opportunityRepository.save(opportunity));
    }

    @Transactional
    public OpportunityResponseDTO updateOpportunityForOrganisation(UUID id, UpdateOpportunityDTO dto, UUID organisationId) {
        Opportunity opportunity = opportunityRepository.findByIdAndOrganisationProfileId(id, organisationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Opportunity not found with id: " + id + " for organisation id: " + organisationId));

        opportunityMapper.updateEntity(opportunity, dto);
        return opportunityMapper.toDTO(opportunityRepository.save(opportunity));
    }

    public void deleteOpportunity(UUID id) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));
        opportunityRepository.delete(opportunity);
    }

    public void deleteOpportunityForOrganisation(UUID id, UUID organisationId) {
        Opportunity opportunity = opportunityRepository.findByIdAndOrganisationProfileId(id, organisationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Opportunity not found with id: " + id + " for organisation id: " + organisationId));
        opportunityRepository.delete(opportunity);
    }

    private boolean containsIgnoreCase(String value, String normalizedQuery) {
        return value != null && value.toLowerCase().contains(normalizedQuery);
    }

    private Comparator<Opportunity> buildOpportunityComparator(String sort, Double latitude, Double longitude) {
        return switch (sort == null ? "newest" : sort.toLowerCase()) {
            case "start-date" -> Comparator.comparing(Opportunity::getStartDate);
            case "hours" -> Comparator.comparing(
                    opportunity -> opportunity.getRequiredHours() == null ? Integer.MAX_VALUE : opportunity.getRequiredHours());
            case "organisation" -> Comparator.comparing(
                    opportunity -> opportunity.getOrganisationProfile().getDisplayName(),
                    String.CASE_INSENSITIVE_ORDER);
            case "closest" -> Comparator.comparing(opportunity -> distanceKm(latitude, longitude, opportunity));
            default -> Comparator.comparing(Opportunity::getDateCreated).reversed();
        };
    }

    private double distanceKm(Double latitude, Double longitude, Opportunity opportunity) {
        if (latitude == null || longitude == null || opportunity.getLatitude() == null || opportunity.getLongitude() == null) {
            return Double.MAX_VALUE;
        }

        double earthRadiusKm = 6371.0;
        double latitudeDelta = Math.toRadians(opportunity.getLatitude() - latitude);
        double longitudeDelta = Math.toRadians(opportunity.getLongitude() - longitude);
        double startLatitude = Math.toRadians(latitude);
        double endLatitude = Math.toRadians(opportunity.getLatitude());

        double a = Math.sin(latitudeDelta / 2) * Math.sin(latitudeDelta / 2)
                + Math.cos(startLatitude) * Math.cos(endLatitude)
                * Math.sin(longitudeDelta / 2) * Math.sin(longitudeDelta / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

}

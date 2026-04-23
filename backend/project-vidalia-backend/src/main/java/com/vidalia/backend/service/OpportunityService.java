package com.vidalia.backend.service;

import com.vidalia.backend.dto.opportunity.CreateOpportunityDTO;
import com.vidalia.backend.dto.opportunity.OpportunityResponseDTO;
import com.vidalia.backend.dto.opportunity.UpdateOpportunityDTO;
import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.mapper.OpportunityMapper;
import com.vidalia.backend.model.Opportunity;
import com.vidalia.backend.model.OrganisationProfile;
import com.vidalia.backend.repository.OpportunityRepository;
import com.vidalia.backend.repository.OrganisationProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        opportunity.setDateCreated(LocalDateTime.now());
        opportunity.setLastUpdated(LocalDateTime.now());
        return opportunityMapper.toDTO(opportunityRepository.save(opportunity));
    }

    @Transactional
    public OpportunityResponseDTO  updateOpportunity(UUID id, UpdateOpportunityDTO dto) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));

        opportunityMapper.updateEntity(opportunity, dto);
        opportunity.setLastUpdated(LocalDateTime.now());
        return opportunityMapper.toDTO(opportunityRepository.save(opportunity));
    }

    @Transactional
    public OpportunityResponseDTO updateOpportunityForOrganisation(UUID id, UpdateOpportunityDTO dto, UUID organisationId) {
        Opportunity opportunity = opportunityRepository.findByIdAndOrganisationProfileId(id, organisationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Opportunity not found with id: " + id + " for organisation id: " + organisationId));

        opportunityMapper.updateEntity(opportunity, dto);
        opportunity.setLastUpdated(LocalDateTime.now());
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



}

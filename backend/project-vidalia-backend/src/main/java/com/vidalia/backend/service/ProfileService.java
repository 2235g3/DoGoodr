package com.vidalia.backend.service;

import com.vidalia.backend.dto.profile.CreateOrganisationProfileDTO;
import com.vidalia.backend.dto.profile.CreateVolunteerProfileDTO;
import com.vidalia.backend.dto.profile.OProfileResponseDTO;
import com.vidalia.backend.dto.profile.UpdateOrganisationProfileDTO;
import com.vidalia.backend.dto.profile.UpdateVolunteerProfileDTO;
import com.vidalia.backend.dto.profile.VProfileResponseDTO;
import com.vidalia.backend.exceptions.ResourceAlreadyExistsException;
import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.mapper.OrganisationProfileMapper;
import com.vidalia.backend.mapper.VolunteerProfileMapper;
import com.vidalia.backend.model.OrganisationProfile;
import com.vidalia.backend.model.VolunteerProfile;
import com.vidalia.backend.repository.OrganisationProfileRepository;
import com.vidalia.backend.repository.VolunteerProfileRepository;
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
public class ProfileService {

    private final VolunteerProfileRepository volunteerRepository;
    private final OrganisationProfileRepository organisationRepository;
    private final UserService userService;
    private final VolunteerProfileMapper volunteerProfileMapper;
    private final OrganisationProfileMapper organisationProfileMapper;

    //=========Volunteer Profile Methods ============

    @Transactional(readOnly = true)
    public List<VProfileResponseDTO> getAllVolunteerProfiles() {
        return volunteerRepository.findAll().stream()
                .map(volunteerProfileMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VProfileResponseDTO getVolunteerProfileById(UUID id) {
        return volunteerRepository.findById(id)
                .map(volunteerProfileMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found with id: " + id));

    }

    @Transactional(readOnly = true)
    public VProfileResponseDTO getVolunteerProfileByUserId(UUID userId) {
        return volunteerRepository.findByUserId(userId)
                .map(volunteerProfileMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found for user id: " + userId));
    }

    @Transactional
    public VProfileResponseDTO createVolunteerProfile(CreateVolunteerProfileDTO dto, UUID userId) {
        //Check if a profile (any type) already exists for this user
        if (volunteerRepository.existsByUserId(userId) || organisationRepository.existsByUserId(userId)) {
            throw new ResourceAlreadyExistsException("Profile already exists for this user");
        }

        VolunteerProfile profile = volunteerProfileMapper.toEntity(dto);
        profile.setLastUpdated(LocalDateTime.now());
        profile.setPointsBalance(0);
        volunteerRepository.save(profile);
        return volunteerProfileMapper.toDTO(profile);
    }

    @Transactional
    public VProfileResponseDTO updateVolunteerProfile(UpdateVolunteerProfileDTO updateDTO, UUID userId) {
        VolunteerProfile volunteerProfile = volunteerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found for user id: " + userId));

        volunteerProfileMapper.updateEntity(volunteerProfile, updateDTO);
        volunteerProfile.setLastUpdated(LocalDateTime.now());
        volunteerRepository.save(volunteerProfile);
        return volunteerProfileMapper.toDTO(volunteerProfile);
    }

    //=========Organisation Profile Methods ============

    @Transactional(readOnly = true)
    public List<OProfileResponseDTO> getAllOrganisationProfiles() {
        return organisationRepository.findAll().stream()
                .map(organisationProfileMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OProfileResponseDTO getOrganisationProfileById(UUID id) {
        return organisationRepository.findById(id)
                .map(organisationProfileMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation profile not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public OProfileResponseDTO getOrganisationProfileByUserId(UUID userId) {
        return organisationRepository.findByUserId(userId)
                .map(organisationProfileMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation profile not found for user id: " + userId));
    }

    @Transactional
    public OProfileResponseDTO createOrganisationProfile(CreateOrganisationProfileDTO dto, UUID userId) {
        // Check if a profile (any type) already exists for this user
        if (volunteerRepository.existsByUserId(userId) || organisationRepository.existsByUserId(userId)) {
            throw new ResourceAlreadyExistsException("Profile already exists for this user");
        }

        OrganisationProfile profile = organisationProfileMapper.toEntity(dto);
        profile.setLastUpdated(LocalDateTime.now());
        profile.setVerified(false);
        organisationRepository.save(profile);
        return organisationProfileMapper.toDTO(profile);
    }

    @Transactional
    public OProfileResponseDTO updateOrganisationProfile(UpdateOrganisationProfileDTO updateDTO, UUID userId) {
        OrganisationProfile organisationProfile = organisationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation profile not found for user id: " + userId));

        organisationProfileMapper.updateEntity(organisationProfile, updateDTO);
        organisationProfile.setLastUpdated(LocalDateTime.now());
        organisationRepository.save(organisationProfile);
        return organisationProfileMapper.toDTO(organisationProfile);
    }

    @Transactional
    public OProfileResponseDTO verifyOrganisationProfile(UUID userId) {
        OrganisationProfile organisationProfile = organisationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation profile not found for user id: " + userId));

        organisationProfile.setVerified(true);
        organisationProfile.setLastUpdated(LocalDateTime.now());
        organisationRepository.save(organisationProfile);
        return organisationProfileMapper.toDTO(organisationProfile);
    }
}

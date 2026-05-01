package com.vidalia.backend.service;

import com.vidalia.backend.config.FileUploadProperties;
import com.vidalia.backend.dto.profile.CreateOrganisationProfileDTO;
import com.vidalia.backend.dto.profile.OProfileResponseDTO;
import com.vidalia.backend.dto.profile.UpdateOrganisationProfileDTO;
import com.vidalia.backend.exceptions.ResourceAlreadyExistsException;
import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.mapper.OrganisationProfileMapper;
import com.vidalia.backend.model.OrganisationProfile;
import com.vidalia.backend.model.User;
import com.vidalia.backend.repository.OrganisationProfileRepository;
import com.vidalia.backend.repository.UserRepository;
import com.vidalia.backend.repository.VolunteerProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OrganisationProfileService {

    private final VolunteerProfileRepository volunteerRepository;
    private final OrganisationProfileRepository organisationRepository;
    private final UserRepository userRepository;
    private final OrganisationProfileMapper organisationProfileMapper;
    private final FileUploadService fileUploadService;
    private final FileUploadProperties fileUploadProperties;

    @Transactional(readOnly = true)
    public List<OProfileResponseDTO> getAllOrganisationProfiles() {
        return organisationRepository.findAll().stream()
                .map(organisationProfileMapper::toDTO)
                .peek(this::applyDefaultProfilePicture)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OProfileResponseDTO getOrganisationProfileById(UUID id) {
        OProfileResponseDTO responseDTO = organisationRepository.findById(id)
                .map(organisationProfileMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation profile not found with id: " + id));
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional(readOnly = true)
    public OProfileResponseDTO getOrganisationProfileByUserId(UUID userId) {
        OProfileResponseDTO responseDTO = organisationRepository.findByUserId(userId)
                .map(organisationProfileMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation profile not found for user id: " + userId));
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public OProfileResponseDTO createOrganisationProfile(CreateOrganisationProfileDTO dto, UUID userId) {
        if (volunteerRepository.existsByUserId(userId) || organisationRepository.existsByUserId(userId)) {
            throw new ResourceAlreadyExistsException("Profile already exists for this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        OrganisationProfile profile = organisationProfileMapper.toEntity(dto);
        profile.setUser(user);
        profile.setVerified(false);

        organisationRepository.save(profile);
        OProfileResponseDTO responseDTO = organisationProfileMapper.toDTO(profile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public OProfileResponseDTO updateOrganisationProfile(UpdateOrganisationProfileDTO updateDTO, UUID userId) {
        OrganisationProfile organisationProfile = organisationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation profile not found for user id: " + userId));

        organisationProfileMapper.updateEntity(organisationProfile, updateDTO);
        organisationRepository.save(organisationProfile);
        OProfileResponseDTO responseDTO = organisationProfileMapper.toDTO(organisationProfile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public OProfileResponseDTO uploadOrganisationProfilePicture(UUID userId, MultipartFile file) {
        OrganisationProfile organisationProfile = organisationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation profile not found for user id: " + userId));

        if (organisationProfile.getProfilePictureUrl() != null && !organisationProfile.getProfilePictureUrl().isBlank()) {
            fileUploadService.deleteFile(organisationProfile.getProfilePictureUrl());
        }

        String storedFileUrl = fileUploadService.uploadProfilePicture(file, userId);
        organisationProfile.setProfilePictureUrl(storedFileUrl);
        organisationRepository.save(organisationProfile);

        OProfileResponseDTO responseDTO = organisationProfileMapper.toDTO(organisationProfile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public OProfileResponseDTO verifyOrganisationProfile(UUID profileId) {
        OrganisationProfile organisationProfile = organisationRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation profile not found with id: " + profileId));

        organisationProfile.setVerified(true);
        organisationRepository.save(organisationProfile);
        OProfileResponseDTO responseDTO = organisationProfileMapper.toDTO(organisationProfile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public void deleteOrganisationProfile(UUID profileId) {
        OrganisationProfile profile = organisationRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + profileId));
        organisationRepository.delete(profile);
    }

    @Transactional
    public OProfileResponseDTO deleteOrganisationProfilePicture(UUID userId) {
        OrganisationProfile organisationProfile = organisationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation profile not found for user id: " + userId));

        if (organisationProfile.getProfilePictureUrl() != null && !organisationProfile.getProfilePictureUrl().isBlank()) {
            fileUploadService.deleteFile(organisationProfile.getProfilePictureUrl());
        }

        organisationProfile.setProfilePictureUrl(null);
        organisationRepository.save(organisationProfile);

        OProfileResponseDTO responseDTO = organisationProfileMapper.toDTO(organisationProfile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    private void applyDefaultProfilePicture(OProfileResponseDTO responseDTO) {
        if (responseDTO.getProfilePictureUrl() == null || responseDTO.getProfilePictureUrl().isBlank()) {
            responseDTO.setProfilePictureUrl(fileUploadProperties.getDefaultProfilePictureUrl());
        }
    }
}

package com.vidalia.backend.service;

import com.vidalia.backend.config.FileUploadProperties;
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
import com.vidalia.backend.model.User;
import com.vidalia.backend.model.VolunteerProfile;
import com.vidalia.backend.repository.OrganisationProfileRepository;
import com.vidalia.backend.repository.UserRepository;
import com.vidalia.backend.repository.VolunteerProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final UserRepository userRepository;
    private final VolunteerProfileMapper volunteerProfileMapper;
    private final OrganisationProfileMapper organisationProfileMapper;
    private final FileUploadService fileUploadService;
    private final FileUploadProperties fileUploadProperties;

    //=========Volunteer Profile Methods ============

    @Transactional(readOnly = true)
    public List<VProfileResponseDTO> getAllVolunteerProfiles() {
        return volunteerRepository.findAll().stream()
                .map(volunteerProfileMapper::toDTO)
                .peek(this::applyDefaultProfilePicture)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VProfileResponseDTO getVolunteerProfileById(UUID id) {
        VProfileResponseDTO responseDTO = volunteerRepository.findById(id)
                .map(volunteerProfileMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found with id: " + id));
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional(readOnly = true)
    public VProfileResponseDTO getVolunteerProfileByUserId(UUID userId) {
        VProfileResponseDTO responseDTO = volunteerRepository.findByUserId(userId)
                .map(volunteerProfileMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found for user id: " + userId));
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public VProfileResponseDTO createVolunteerProfile(CreateVolunteerProfileDTO dto, UUID userId) {
        if (volunteerRepository.existsByUserId(userId) || organisationRepository.existsByUserId(userId)) {
            throw new ResourceAlreadyExistsException("Profile already exists for this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        VolunteerProfile profile = volunteerProfileMapper.toEntity(dto);
        profile.setUser(user);
        profile.setLastUpdated(LocalDateTime.now());
        profile.setPointsBalance(0);

        volunteerRepository.save(profile);
        VProfileResponseDTO responseDTO = volunteerProfileMapper.toDTO(profile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public VProfileResponseDTO updateVolunteerProfile(UpdateVolunteerProfileDTO updateDTO, UUID userId) {
        VolunteerProfile volunteerProfile = volunteerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found for user id: " + userId));

        volunteerProfileMapper.updateEntity(volunteerProfile, updateDTO);
        volunteerProfile.setLastUpdated(LocalDateTime.now());
        volunteerRepository.save(volunteerProfile);
        VProfileResponseDTO responseDTO = volunteerProfileMapper.toDTO(volunteerProfile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public VProfileResponseDTO uploadVolunteerProfilePicture(UUID userId, MultipartFile file) {
        VolunteerProfile volunteerProfile = volunteerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found for user id: " + userId));

        if (volunteerProfile.getProfilePictureUrl() != null && !volunteerProfile.getProfilePictureUrl().isBlank()) {
            fileUploadService.deleteFile(volunteerProfile.getProfilePictureUrl());
        }

        String storedFileUrl = fileUploadService.uploadProfilePicture(file, userId);
        volunteerProfile.setProfilePictureUrl(storedFileUrl);
        volunteerProfile.setLastUpdated(LocalDateTime.now());
        volunteerRepository.save(volunteerProfile);

        VProfileResponseDTO responseDTO = volunteerProfileMapper.toDTO(volunteerProfile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public VProfileResponseDTO uploadVolunteerCV(UUID userId, MultipartFile file) {
        VolunteerProfile volunteerProfile = volunteerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found for user id: " + userId));

        if (volunteerProfile.getCvUrl() != null && !volunteerProfile.getCvUrl().isBlank()) {
            fileUploadService.deleteFile(volunteerProfile.getCvUrl());
        }

        String storedFileUrl = fileUploadService.uploadCV(file, userId);
        volunteerProfile.setCvUrl(storedFileUrl);
        volunteerProfile.setLastUpdated(LocalDateTime.now());
        volunteerRepository.save(volunteerProfile);

        VProfileResponseDTO responseDTO = volunteerProfileMapper.toDTO(volunteerProfile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public VProfileResponseDTO deleteVolunteerProfilePicture(UUID userId) {
        VolunteerProfile volunteerProfile = volunteerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found for user id: " + userId));

        if (volunteerProfile.getProfilePictureUrl() != null && !volunteerProfile.getProfilePictureUrl().isBlank()) {
            fileUploadService.deleteFile(volunteerProfile.getProfilePictureUrl());
        }

        volunteerProfile.setProfilePictureUrl(null);
        volunteerProfile.setLastUpdated(LocalDateTime.now());
        volunteerRepository.save(volunteerProfile);

        VProfileResponseDTO responseDTO = volunteerProfileMapper.toDTO(volunteerProfile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public VProfileResponseDTO deleteVolunteerCV(UUID userId) {
        VolunteerProfile volunteerProfile = volunteerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found for user id: " + userId));

        if (volunteerProfile.getCvUrl() != null && !volunteerProfile.getCvUrl().isBlank()) {
            fileUploadService.deleteFile(volunteerProfile.getCvUrl());
        }

        volunteerProfile.setCvUrl(null);
        volunteerProfile.setLastUpdated(LocalDateTime.now());
        volunteerRepository.save(volunteerProfile);

        VProfileResponseDTO responseDTO = volunteerProfileMapper.toDTO(volunteerProfile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public void deleteVolunteerProfile(UUID profileId) {
        VolunteerProfile profile = volunteerRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + profileId));
        volunteerRepository.delete(profile);
    }

    //=========Organisation Profile Methods ============

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
        profile.setLastUpdated(LocalDateTime.now());
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
        organisationProfile.setLastUpdated(LocalDateTime.now());
        organisationRepository.save(organisationProfile);
        OProfileResponseDTO responseDTO = organisationProfileMapper.toDTO(organisationProfile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    @Transactional
    public OProfileResponseDTO uploadOrganisationProfilePicture(UUID userId, MultipartFile file) {
        OrganisationProfile organisationProfile = organisationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation profile not found for user id: " + userId));

        String storedFileUrl = fileUploadService.uploadProfilePicture(file, userId);
        organisationProfile.setProfilePictureUrl(storedFileUrl);
        organisationProfile.setLastUpdated(LocalDateTime.now());
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
        organisationProfile.setLastUpdated(LocalDateTime.now());
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
        organisationProfile.setLastUpdated(LocalDateTime.now());
        organisationRepository.save(organisationProfile);

        OProfileResponseDTO responseDTO = organisationProfileMapper.toDTO(organisationProfile);
        applyDefaultProfilePicture(responseDTO);
        return responseDTO;
    }

    private void applyDefaultProfilePicture(VProfileResponseDTO responseDTO) {
        if (responseDTO.getProfilePictureUrl() == null || responseDTO.getProfilePictureUrl().isBlank()) {
            responseDTO.setProfilePictureUrl(fileUploadProperties.getDefaultProfilePictureUrl());
        }
    }

    private void applyDefaultProfilePicture(OProfileResponseDTO responseDTO) {
        if (responseDTO.getProfilePictureUrl() == null || responseDTO.getProfilePictureUrl().isBlank()) {
            responseDTO.setProfilePictureUrl(fileUploadProperties.getDefaultProfilePictureUrl());
        }
    }
}

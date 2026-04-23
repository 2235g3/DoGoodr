package com.vidalia.backend.mapper;

import com.vidalia.backend.dto.profile.CreateVolunteerProfileDTO;
import com.vidalia.backend.dto.profile.UpdateVolunteerProfileDTO;
import com.vidalia.backend.dto.profile.VProfileResponseDTO;
import com.vidalia.backend.model.VolunteerProfile;
import org.springframework.stereotype.Component;

@Component
public class VolunteerProfileMapper {

    public VolunteerProfile toEntity(CreateVolunteerProfileDTO dto) {
        VolunteerProfile profile = new VolunteerProfile();
        profile.setForename(dto.getForename());
        profile.setSurname(dto.getSurname());
        profile.setPreferredName(dto.getPreferredName());
        profile.setContactEmail(dto.getContactEmail());
        profile.setLocation(dto.getLocation());
        profile.setProfileDescription(dto.getProfileDescription());
        profile.setLongitude(dto.getLongitude());
        profile.setLatitude(dto.getLatitude());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setMaxTravelDistance(dto.getMaxTravelDistance());
        return profile;
    }

    public VProfileResponseDTO toDTO(VolunteerProfile profile) {
        VProfileResponseDTO responseDTO = new VProfileResponseDTO();
        responseDTO.setId(profile.getId());
        responseDTO.setForename(profile.getForename());
        responseDTO.setSurname(profile.getSurname());
        responseDTO.setPreferredName(profile.getPreferredName());
        responseDTO.setProfilePictureUrl(profile.getProfilePictureUrl());
        responseDTO.setCvUrl(profile.getCvUrl());
        responseDTO.setContactEmail(profile.getContactEmail());
        responseDTO.setLocation(profile.getLocation());
        responseDTO.setProfileDescription(profile.getProfileDescription());
        responseDTO.setLongitude(profile.getLongitude());
        responseDTO.setLatitude(profile.getLatitude());
        responseDTO.setMaxTravelDistance(profile.getMaxTravelDistance());
        responseDTO.setRemoteOnly(profile.isRemoteOnly());
        responseDTO.setTotalHours(profile.getTotalHours());
        responseDTO.setAvailability(profile.getAvailability());
        responseDTO.setDateOfBirth(profile.getDateOfBirth());
        responseDTO.setLastUpdated(profile.getLastUpdated());
        responseDTO.setPointsBalance(profile.getPointsBalance());
        return responseDTO;
    }

    public void updateEntity(VolunteerProfile profile, UpdateVolunteerProfileDTO dto) {
        if (dto.getForename() != null) {
            profile.setForename(dto.getForename());
        }
        if (dto.getSurname() != null) {
            profile.setSurname(dto.getSurname());
        }
        if (dto.getPreferredName() != null) {
            profile.setPreferredName(dto.getPreferredName());
        }
        if (dto.getContactEmail() != null) {
            profile.setContactEmail(dto.getContactEmail());
        }
        if (dto.getLocation() != null) {
            profile.setLocation(dto.getLocation());
        }
        if (dto.getProfileDescription() != null) {
            profile.setProfileDescription(dto.getProfileDescription());
        }
        if (dto.getMaxTravelDistance() != null) {
            profile.setMaxTravelDistance(dto.getMaxTravelDistance());
        }
        if (dto.getAvailability() != null) {
            profile.setAvailability(dto.getAvailability());
        }
    }



}

package com.vidalia.backend.mapper;

import com.vidalia.backend.dto.profile.CreateOrganisationProfileDTO;
import com.vidalia.backend.dto.profile.OProfileResponseDTO;
import com.vidalia.backend.dto.profile.UpdateOrganisationProfileDTO;
import com.vidalia.backend.model.OrganisationProfile;
import org.springframework.stereotype.Component;

@Component
public class OrganisationProfileMapper {

    public OrganisationProfile toEntity(CreateOrganisationProfileDTO dto) {
        OrganisationProfile profile = new OrganisationProfile();
        profile.setDisplayName(dto.getDisplayName());
        profile.setAccountType(dto.getAccountType());
        profile.setDescription(dto.getDescription());
        profile.setContactEmail(dto.getContactEmail());
        profile.setLocation(dto.getLocation());
        profile.setWebsiteUrl(dto.getWebsiteUrl());
        return profile;
    }

    public OProfileResponseDTO toDTO(OrganisationProfile organisationProfile) {
        OProfileResponseDTO response = new OProfileResponseDTO();
        response.setId(organisationProfile.getId());
        response.setDisplayName(organisationProfile.getDisplayName());
        response.setProfilePictureUrl(organisationProfile.getProfilePictureUrl());
        response.setDescription(organisationProfile.getDescription());
        response.setContactEmail(organisationProfile.getContactEmail());
        response.setLocation(organisationProfile.getLocation());
        response.setWebsiteUrl(organisationProfile.getWebsiteUrl());
        return response;
    }

    public void updateEntity(OrganisationProfile profile, UpdateOrganisationProfileDTO dto) {
        if (dto.getDisplayName() != null) {
            profile.setDisplayName(dto.getDisplayName());
        }
        if (dto.getAccountType() != null) {
            profile.setAccountType(dto.getAccountType());
        }
        if (dto.getDescription() != null) {
            profile.setDescription(dto.getDescription());
        }
        if (dto.getContactEmail() != null) {
            profile.setContactEmail(dto.getContactEmail());
        }
        if (dto.getLocation() != null) {
            profile.setLocation(dto.getLocation());
        }
        if (dto.getWebsiteUrl() != null) {
            profile.setWebsiteUrl(dto.getWebsiteUrl());
        }
    }
}

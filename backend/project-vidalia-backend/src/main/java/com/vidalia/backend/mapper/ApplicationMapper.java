package com.vidalia.backend.mapper;

import com.vidalia.backend.dto.application.ApplicationResponseDTO;
import com.vidalia.backend.dto.application.CreateApplicationDTO;
import com.vidalia.backend.model.Application;
import com.vidalia.backend.model.Opportunity;
import com.vidalia.backend.model.VolunteerProfile;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {

    public Application toEntity(VolunteerProfile volunteerProfile, Opportunity opportunity, CreateApplicationDTO createDTO) {
        Application application = new Application();
        application.setVolunteerProfile(volunteerProfile);
        application.setOpportunity(opportunity);
        application.setMessage(createDTO.getMessage());
        // status and dateApplied will be set by defaults / lifecycle hooks
        return application;
    }

    public ApplicationResponseDTO toDTO(Application application) {
        ApplicationResponseDTO dto = new ApplicationResponseDTO();
        if (application == null) return dto;

        dto.setId(application.getId());

        VolunteerProfile vp = application.getVolunteerProfile();
        if (vp != null) {
            dto.setVolunteerId(vp.getId());
            String name = buildVolunteerName(vp);
            dto.setVolunteerName(name);
        }

        Opportunity opp = application.getOpportunity();
        if (opp != null) {
            dto.setOpportunityId(opp.getId());
            dto.setOpportunityName(opp.getTitle());
        }

        dto.setMessage(application.getMessage());
        dto.setStatus(application.getStatus() != null ? application.getStatus().name() : null);
        dto.setDateApplied(application.getDateApplied());
        dto.setDecisionDate(application.getDecisionDate());
        return dto;
    }

    private String buildVolunteerName(VolunteerProfile vp) {
        String preferred = vp.getPreferredName();
        String forename = vp.getForename();
        String surname = vp.getSurname();

        // Use preferred name if available, otherwise forename. If both are null/blank, use empty string.
        String first = preferred != null && !preferred.isBlank() ? preferred : forename;
        if (first == null) first = "";
        if (surname != null && !surname.isBlank()) {
            return (first + " " + surname).trim();
        }
        return first.trim();
    }

}

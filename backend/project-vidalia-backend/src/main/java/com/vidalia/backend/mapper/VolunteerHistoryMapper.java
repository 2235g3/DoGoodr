package com.vidalia.backend.mapper;

import com.vidalia.backend.dto.volunteerHistory.CreateVolunteerHistoryDTO;
import com.vidalia.backend.dto.volunteerHistory.VolunteerHistoryResponseDTO;
import com.vidalia.backend.model.Opportunity;
import com.vidalia.backend.model.VolunteerHistory;
import com.vidalia.backend.model.VolunteerProfile;
import org.springframework.stereotype.Component;

@Component
public class VolunteerHistoryMapper {

    public VolunteerHistory toEntity(CreateVolunteerHistoryDTO createDTO) {
        VolunteerHistory history = new VolunteerHistory();
        history.setStartDate(createDTO.getStartDate());
        history.setEndDate(createDTO.getEndDate());
        history.setHoursLogged(0);
        history.setPointsEarned(0);
        history.setOrganisationComment("");
        // volunteerProfile and opportunity will be set by the service
        return history;
    }

    public VolunteerHistoryResponseDTO toDTO(VolunteerHistory history) {
        VolunteerHistoryResponseDTO dto = new VolunteerHistoryResponseDTO();
        if (history == null) return dto;

        dto.setId(history.getId());

        VolunteerProfile vp = history.getVolunteerProfile();
        if (vp != null) {
            dto.setVolunteerId(vp.getId());
            dto.setVolunteerName(buildVolunteerName(vp));
        }

        Opportunity opp = history.getOpportunity();
        if (opp != null) {
            dto.setOpportunityId(opp.getId());
            dto.setOpportunityTitle(opp.getTitle());

            // Get organisation info from opportunity
            if (opp.getOrganisationProfile() != null) {
                dto.setOrganisationId(opp.getOrganisationProfile().getId());
                dto.setOrganisationName(opp.getOrganisationProfile().getDisplayName());
            }
        }

        dto.setHoursLogged(history.getHoursLogged());
        dto.setStartDate(history.getStartDate());
        dto.setEndDate(history.getEndDate());
        dto.setPointsGained(history.getPointsEarned());
        dto.setOrganisationComment(history.getOrganisationComment());
        return dto;
    }

    private String buildVolunteerName(VolunteerProfile vp) {
        String preferred = vp.getPreferredName();
        String forename = vp.getForename();
        String surname = vp.getSurname();

        String first = preferred != null && !preferred.isBlank() ? preferred : forename;
        if (first == null) first = "";
        if (surname != null && !surname.isBlank()) {
            return (first + " " + surname).trim();
        }
        return first.trim();
    }
}

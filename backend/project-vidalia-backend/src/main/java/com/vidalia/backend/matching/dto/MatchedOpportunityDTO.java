package com.vidalia.backend.matching.dto;

import com.vidalia.backend.dto.opportunity.OpportunityResponseDTO;
import lombok.Data;

@Data
public class MatchedOpportunityDTO {
    private OpportunityResponseDTO opportunity;
    private double finalScore;
    private Double normalizedScore;
    private Double locationScore;
}

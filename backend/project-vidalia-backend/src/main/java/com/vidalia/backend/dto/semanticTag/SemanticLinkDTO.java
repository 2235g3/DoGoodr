package com.vidalia.backend.dto.semanticTag;

import com.vidalia.backend.model.matchmaking.SemanticTag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SemanticLinkDTO {
    @NotNull
    private SemanticTag one;
    @NotNull
    private SemanticTag two;
    @NotNull
    private double weight;
}

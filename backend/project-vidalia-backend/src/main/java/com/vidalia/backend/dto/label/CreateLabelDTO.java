package com.vidalia.backend.dto.label;

import com.vidalia.backend.model.matchmaking.LabelType;
import com.vidalia.backend.model.matchmaking.SemanticTag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLabelDTO {
    @NotNull
    private String name;
    private SemanticTag semanticTag;
    @NotNull
    private boolean required;
    @NotNull
    private LabelType type;
}

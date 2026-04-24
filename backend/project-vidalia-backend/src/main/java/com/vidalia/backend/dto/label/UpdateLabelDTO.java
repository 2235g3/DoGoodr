package com.vidalia.backend.dto.label;

import com.vidalia.backend.model.matchmaking.LabelType;
import com.vidalia.backend.model.matchmaking.SemanticTag;
import lombok.Data;

@Data
public class UpdateLabelDTO {
    private String name;
    private SemanticTag semanticTag;
    private Boolean required;
    private LabelType type;
}

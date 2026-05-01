package com.vidalia.backend.dto.label;

import com.vidalia.backend.model.matchmaking.LabelType;
import lombok.Data;

@Data
public class LabelDTO {
    private Long id;
    private String name;
    private String semanticTag;
    private boolean required;
    private LabelType type;
}

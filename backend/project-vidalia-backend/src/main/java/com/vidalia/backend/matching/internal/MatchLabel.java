package com.vidalia.backend.matching.internal;

import com.vidalia.backend.model.matchmaking.Label;
import com.vidalia.backend.model.matchmaking.LabelType;

public record MatchLabel(
        Long id,
        String name,
        LabelType type,
        boolean required,
        Long semanticTagId,
        double weight,
        boolean explicit
) {

    public static MatchLabel fromLabel(Label label, double weight, boolean explicit) {
        Long semanticTagId = label.getSemanticTag() != null ? label.getSemanticTag().getId() : null;
        return new MatchLabel(
                label.getId(),
                label.getName(),
                label.getType(),
                label.isRequired(),
                semanticTagId,
                weight,
                explicit
        );
    }
}

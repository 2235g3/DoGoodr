package com.vidalia.backend.matching.internal;

import com.vidalia.backend.model.Opportunity;
import com.vidalia.backend.model.matchmaking.OpportunityLabelLink;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record OpportunityMatchCandidate(
        Opportunity opportunity,
        Map<Long, OpportunityLabelLink> labelsById
) {
    public OpportunityMatchCandidate {
        labelsById = Collections.unmodifiableMap(new LinkedHashMap<>(labelsById));
    }
}

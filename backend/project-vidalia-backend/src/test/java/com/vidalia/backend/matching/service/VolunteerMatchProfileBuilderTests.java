package com.vidalia.backend.matching.service;

import com.vidalia.backend.matching.internal.MatchLabel;
import com.vidalia.backend.model.matchmaking.Label;
import com.vidalia.backend.model.matchmaking.LabelType;
import com.vidalia.backend.model.matchmaking.SemanticLink;
import com.vidalia.backend.model.matchmaking.SemanticTag;
import com.vidalia.backend.model.matchmaking.VolunteerLabelLink;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VolunteerMatchProfileBuilderTests {

    private final VolunteerMatchProfileBuilder builder = new VolunteerMatchProfileBuilder();

    @Test
    void buildAllLabelsLoadsExplicitLabels() {
        UUID volunteerId = UUID.randomUUID();
        SemanticTag gardeningTag = semanticTag(1L, "gardening");
        Label gardening = label(10L, "Gardening", LabelType.SKILL, false, gardeningTag);

        VolunteerLabelLink explicitLink = new VolunteerLabelLink(volunteerId, gardening, 0.8d);

        Map<Long, MatchLabel> allLabels = builder.buildAllLabels(
                List.of(explicitLink),
                List.of(gardening),
                List.of()
        );

        assertEquals(1, allLabels.size());
        assertTrue(allLabels.containsKey(gardening.getId()));
        assertTrue(allLabels.get(gardening.getId()).explicit());
        assertEquals(0.8d, allLabels.get(gardening.getId()).weight());
    }

    @Test
    void buildInfersOneHopSemanticLabelsAboveThreshold() {
        UUID volunteerId = UUID.randomUUID();
        SemanticTag gardeningTag = semanticTag(1L, "gardening");
        SemanticTag environmentTag = semanticTag(2L, "environment");
        Label gardening = label(10L, "Gardening", LabelType.SKILL, false, gardeningTag);
        Label litterPicking = label(11L, "Litter Picking", LabelType.CAUSE, false, environmentTag);

        VolunteerLabelLink explicitLink = new VolunteerLabelLink(volunteerId, gardening, 0.8d);
        SemanticLink semanticLink = new SemanticLink(gardeningTag, environmentTag, 0.5d);

        Map<Long, MatchLabel> allLabels = builder.buildAllLabels(
                List.of(explicitLink),
                List.of(gardening, litterPicking),
                List.of(semanticLink)
        );

        MatchLabel inferredLabel = allLabels.get(litterPicking.getId());
        assertNotNull(inferredLabel);
        assertFalse(inferredLabel.explicit());
        assertEquals(0.4d, inferredLabel.weight(), 1.0e-9);
    }

    @Test
    void buildSkipsInferredLabelsAtOrBelowThreshold() {
        UUID volunteerId = UUID.randomUUID();
        SemanticTag gardeningTag = semanticTag(1L, "gardening");
        SemanticTag environmentTag = semanticTag(2L, "environment");
        Label gardening = label(10L, "Gardening", LabelType.SKILL, false, gardeningTag);
        Label litterPicking = label(11L, "Litter Picking", LabelType.CAUSE, false, environmentTag);

        VolunteerLabelLink explicitLink = new VolunteerLabelLink(volunteerId, gardening, 0.4d);
        SemanticLink semanticLink = new SemanticLink(gardeningTag, environmentTag, 0.5d);

        Map<Long, MatchLabel> allLabels = builder.buildAllLabels(
                List.of(explicitLink),
                List.of(gardening, litterPicking),
                List.of(semanticLink)
        );

        assertFalse(allLabels.containsKey(litterPicking.getId()));
    }

    @Test
    void buildKeepsExplicitLabelWhenSemanticExpansionFindsSameLabel() {
        UUID volunteerId = UUID.randomUUID();
        SemanticTag gardeningTag = semanticTag(1L, "gardening");
        SemanticTag environmentTag = semanticTag(2L, "environment");
        Label gardening = label(10L, "Gardening", LabelType.SKILL, false, gardeningTag);
        Label litterPicking = label(11L, "Litter Picking", LabelType.CAUSE, false, environmentTag);

        VolunteerLabelLink gardeningLink = new VolunteerLabelLink(volunteerId, gardening, 0.7d);
        VolunteerLabelLink litterPickingLink = new VolunteerLabelLink(volunteerId, litterPicking, 0.9d);
        SemanticLink semanticLink = new SemanticLink(gardeningTag, environmentTag, 0.8d);

        Map<Long, MatchLabel> allLabels = builder.buildAllLabels(
                List.of(gardeningLink, litterPickingLink),
                List.of(gardening, litterPicking),
                List.of(semanticLink)
        );

        assertEquals(2, allLabels.size());
        assertTrue(allLabels.containsKey(litterPicking.getId()));
        assertTrue(allLabels.get(litterPicking.getId()).explicit());
        assertEquals(0.9d, allLabels.get(litterPicking.getId()).weight(), 1.0e-9);
    }

    @Test
    void buildKeepsHighestInferredWeightWhenMultipleSemanticPathsReachSameLabel() {
        UUID volunteerId = UUID.randomUUID();
        SemanticTag gardeningTag = semanticTag(1L, "gardening");
        SemanticTag cookingTag = semanticTag(2L, "cooking");
        SemanticTag environmentTag = semanticTag(3L, "environment");
        Label gardening = label(10L, "Gardening", LabelType.SKILL, false, gardeningTag);
        Label cooking = label(11L, "Cooking", LabelType.SKILL, false, cookingTag);
        Label litterPicking = label(12L, "Litter Picking", LabelType.CAUSE, false, environmentTag);

        VolunteerLabelLink gardeningLink = new VolunteerLabelLink(volunteerId, gardening, 0.6d);
        VolunteerLabelLink cookingLink = new VolunteerLabelLink(volunteerId, cooking, 0.9d);
        SemanticLink gardeningToEnvironment = new SemanticLink(gardeningTag, environmentTag, 0.4d);
        SemanticLink cookingToEnvironment = new SemanticLink(cookingTag, environmentTag, 0.5d);

        Map<Long, MatchLabel> allLabels = builder.buildAllLabels(
                List.of(gardeningLink, cookingLink),
                List.of(gardening, cooking, litterPicking),
                List.of(gardeningToEnvironment, cookingToEnvironment)
        );

        MatchLabel inferredLabel = allLabels.get(litterPicking.getId());
        assertNotNull(inferredLabel);
        assertFalse(inferredLabel.explicit());
        assertEquals(0.45d, inferredLabel.weight(), 1.0e-9);
    }

    private SemanticTag semanticTag(Long id, String name) {
        SemanticTag semanticTag = new SemanticTag();
        semanticTag.setId(id);
        semanticTag.setName(name);
        return semanticTag;
    }

    private Label label(Long id, String name, LabelType type, boolean required, SemanticTag semanticTag) {
        Label label = new Label();
        label.setId(id);
        label.setName(name);
        label.setType(type);
        label.setRequired(required);
        label.setSemanticTag(semanticTag);
        return label;
    }
}

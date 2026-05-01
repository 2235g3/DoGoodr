package com.vidalia.backend.matching.service;

import com.vidalia.backend.exceptions.ResourceNotFoundException;
import com.vidalia.backend.matching.internal.MatchLabel;
import com.vidalia.backend.model.matchmaking.Label;
import com.vidalia.backend.model.matchmaking.LabelType;
import com.vidalia.backend.model.matchmaking.SemanticLink;
import com.vidalia.backend.model.matchmaking.SemanticTag;
import com.vidalia.backend.model.matchmaking.VolunteerLabelLink;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class VolunteerMatchProfileBuilder {

    static final double LABEL_WEIGHT_THRESHOLD = 0.20d;

    private static final Set<LabelType> MATCHABLE_TYPES = EnumSet.of(
            LabelType.SKILL,
            LabelType.INTEREST,
            LabelType.CAUSE
    );

    public Map<Long, MatchLabel> buildAllLabels(List<VolunteerLabelLink> explicitLinks,
                                                Collection<Label> cachedLabels,
                                                Collection<SemanticLink> semanticLinks) {
        Map<Long, Label> labelsById = new HashMap<>();
        Map<Long, List<Label>> labelsBySemanticTagId = new HashMap<>();

        for (Label label : cachedLabels) {
            labelsById.put(label.getId(), label);

            if (label.getSemanticTag() != null && MATCHABLE_TYPES.contains(label.getType())) {
                labelsBySemanticTagId
                        .computeIfAbsent(label.getSemanticTag().getId(), ignored -> new ArrayList<>())
                        .add(label);
            }
        }

        Map<Long, List<RelatedSemanticTag>> semanticLinksByTagId = buildSemanticLinksByTagId(semanticLinks);
        Map<Long, MatchLabel> explicitLabels = buildExplicitLabels(explicitLinks, labelsById);
        Map<Long, MatchLabel> inferredLabels = buildInferredLabels(explicitLabels, labelsBySemanticTagId, semanticLinksByTagId);

        Map<Long, MatchLabel> allLabels = new LinkedHashMap<>(inferredLabels);
        allLabels.putAll(explicitLabels);
        return allLabels;
    }

    private Map<Long, MatchLabel> buildExplicitLabels(List<VolunteerLabelLink> explicitLinks, Map<Long, Label> labelsById) {
        Map<Long, MatchLabel> explicitLabels = new LinkedHashMap<>();

        for (VolunteerLabelLink explicitLink : explicitLinks) {
            Label linkedLabel = explicitLink.getLabel();
            Long labelId = linkedLabel != null ? linkedLabel.getId() : null;

            if (labelId == null) {
                throw new ResourceNotFoundException("Volunteer label link is missing a label reference");
            }

            Label canonicalLabel = labelsById.get(labelId);
            if (canonicalLabel == null) {
                throw new ResourceNotFoundException("Label not found with id: " + labelId);
            }

            explicitLabels.put(labelId, MatchLabel.fromLabel(canonicalLabel, explicitLink.getWeight(), true));
        }

        return explicitLabels;
    }

    private Map<Long, MatchLabel> buildInferredLabels(Map<Long, MatchLabel> explicitLabels,
                                                      Map<Long, List<Label>> labelsBySemanticTagId,
                                                      Map<Long, List<RelatedSemanticTag>> semanticLinksByTagId) {
        Map<Long, MatchLabel> inferredLabels = new LinkedHashMap<>();

        for (MatchLabel explicitLabel : explicitLabels.values()) {
            if (explicitLabel.semanticTagId() == null) {
                continue;
            }

            List<RelatedSemanticTag> relatedTags = semanticLinksByTagId.getOrDefault(explicitLabel.semanticTagId(), List.of());
            for (RelatedSemanticTag relatedTag : relatedTags) {
                List<Label> relatedLabels = labelsBySemanticTagId.getOrDefault(relatedTag.tagId(), List.of());
                for (Label relatedLabel : relatedLabels) {
                    if (explicitLabels.containsKey(relatedLabel.getId())) {
                        continue;
                    }

                    double inferredWeight = explicitLabel.weight() * relatedTag.weight();
                    if (inferredWeight <= LABEL_WEIGHT_THRESHOLD) {
                        continue;
                    }

                    MatchLabel existingMatch = inferredLabels.get(relatedLabel.getId());
                    if (existingMatch == null || inferredWeight > existingMatch.weight()) {
                        inferredLabels.put(relatedLabel.getId(), MatchLabel.fromLabel(relatedLabel, inferredWeight, false));
                    }
                }
            }
        }

        return inferredLabels;
    }

    private Map<Long, List<RelatedSemanticTag>> buildSemanticLinksByTagId(Collection<SemanticLink> semanticLinks) {
        Map<Long, List<RelatedSemanticTag>> semanticLinksByTagId = new HashMap<>();

        for (SemanticLink semanticLink : semanticLinks) {
            SemanticTag tagOne = semanticLink.getTagOne();
            SemanticTag tagTwo = semanticLink.getTagTwo();
            if (tagOne == null || tagTwo == null) {
                continue;
            }

            semanticLinksByTagId
                    .computeIfAbsent(tagOne.getId(), ignored -> new ArrayList<>())
                    .add(new RelatedSemanticTag(tagTwo.getId(), semanticLink.getWeight()));
            semanticLinksByTagId
                    .computeIfAbsent(tagTwo.getId(), ignored -> new ArrayList<>())
                    .add(new RelatedSemanticTag(tagOne.getId(), semanticLink.getWeight()));
        }

        return semanticLinksByTagId;
    }

    private record RelatedSemanticTag(Long tagId, double weight) {
    }
}

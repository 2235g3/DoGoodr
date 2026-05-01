package com.vidalia.backend.config;

import com.vidalia.backend.model.matchmaking.Label;
import com.vidalia.backend.model.matchmaking.LabelType;
import com.vidalia.backend.model.matchmaking.SemanticLink;
import com.vidalia.backend.model.matchmaking.SemanticTag;
import com.vidalia.backend.repository.matchmaking.LabelRepository;
import com.vidalia.backend.repository.matchmaking.SemanticLinkRepository;
import com.vidalia.backend.repository.matchmaking.SemanticTagRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MatchingTaxonomySeeder implements ApplicationRunner {

    private final LabelRepository labelRepository;
    private final SemanticLinkRepository semanticLinkRepository;
    private final SemanticTagRepository semanticTagRepository;

    public MatchingTaxonomySeeder(LabelRepository labelRepository,
                                  SemanticLinkRepository semanticLinkRepository,
                                  SemanticTagRepository semanticTagRepository) {
        this.labelRepository = labelRepository;
        this.semanticLinkRepository = semanticLinkRepository;
        this.semanticTagRepository = semanticTagRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, SemanticTag> tags = seedTags(List.of(
                "community support",
                "environment",
                "education",
                "health and wellbeing",
                "animals",
                "arts and culture",
                "digital skills",
                "languages",
                "sports",
                "advocacy"
        ));

        seedLabel("Mentoring", LabelType.SKILL, tags.get("education"));
        seedLabel("Tutoring", LabelType.SKILL, tags.get("education"));
        seedLabel("Youth work", LabelType.CAUSE, tags.get("community support"));
        seedLabel("Food banks", LabelType.CAUSE, tags.get("community support"));
        seedLabel("Elder support", LabelType.CAUSE, tags.get("community support"));
        seedLabel("Mental health", LabelType.CAUSE, tags.get("health and wellbeing"));
        seedLabel("First aid", LabelType.SKILL, tags.get("health and wellbeing"));
        seedLabel("Conservation", LabelType.CAUSE, tags.get("environment"));
        seedLabel("Litter picking", LabelType.INTEREST, tags.get("environment"));
        seedLabel("Animal care", LabelType.CAUSE, tags.get("animals"));
        seedLabel("Event support", LabelType.SKILL, tags.get("arts and culture"));
        seedLabel("Social media", LabelType.SKILL, tags.get("digital skills"));
        seedLabel("Web design", LabelType.SKILL, tags.get("digital skills"));
        seedLabel("English", LabelType.LANGUAGE, tags.get("languages"));
        seedLabel("Spanish", LabelType.LANGUAGE, tags.get("languages"));
        seedLabel("Coaching", LabelType.SKILL, tags.get("sports"));
        seedLabel("Fundraising", LabelType.SKILL, tags.get("advocacy"));
        seedLabel("Campaigning", LabelType.SKILL, tags.get("advocacy"));

        seedLink(tags.get("community support"), tags.get("health and wellbeing"), 0.75);
        seedLink(tags.get("education"), tags.get("community support"), 0.65);
        seedLink(tags.get("digital skills"), tags.get("advocacy"), 0.6);
        seedLink(tags.get("environment"), tags.get("animals"), 0.55);
    }

    private Map<String, SemanticTag> seedTags(List<String> names) {
        Map<String, SemanticTag> tags = new HashMap<>();
        for (String name : names) {
            SemanticTag tag = semanticTagRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> {
                        SemanticTag nextTag = new SemanticTag();
                        nextTag.setName(name);
                        return semanticTagRepository.save(nextTag);
                    });
            tags.put(name, tag);
        }
        return tags;
    }

    private void seedLabel(String name, LabelType type, SemanticTag semanticTag) {
        if (labelRepository.existsByNameIgnoreCase(name)) {
            return;
        }

        Label label = new Label();
        label.setName(name);
        label.setType(type);
        label.setRequired(false);
        label.setSemanticTag(semanticTag);
        labelRepository.save(label);
    }

    private void seedLink(SemanticTag firstTag, SemanticTag secondTag, double weight) {
        if (firstTag == null || secondTag == null || firstTag.getId().equals(secondTag.getId())) {
            return;
        }

        SemanticTag tagOne = firstTag.getId() < secondTag.getId() ? firstTag : secondTag;
        SemanticTag tagTwo = firstTag.getId() < secondTag.getId() ? secondTag : firstTag;
        if (semanticLinkRepository.existsByTagOne_IdAndTagTwo_Id(tagOne.getId(), tagTwo.getId())) {
            return;
        }

        semanticLinkRepository.save(new SemanticLink(tagOne, tagTwo, weight));
    }
}

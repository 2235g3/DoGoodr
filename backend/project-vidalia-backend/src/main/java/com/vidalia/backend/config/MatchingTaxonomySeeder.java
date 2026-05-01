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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MatchingTaxonomySeeder implements ApplicationRunner {

    private final LabelRepository labelRepository;
    private final JdbcTemplate jdbcTemplate;
    private final SemanticLinkRepository semanticLinkRepository;
    private final SemanticTagRepository semanticTagRepository;
    private final DataSource dataSource;

    public MatchingTaxonomySeeder(LabelRepository labelRepository,
                                  JdbcTemplate jdbcTemplate,
                                  SemanticLinkRepository semanticLinkRepository,
                                  SemanticTagRepository semanticTagRepository,
                                  DataSource dataSource) {
        this.labelRepository = labelRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.semanticLinkRepository = semanticLinkRepository;
        this.semanticTagRepository = semanticTagRepository;
        this.dataSource = dataSource;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        repairLegacySemanticTagForeignKeys();

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

    private void repairLegacySemanticTagForeignKeys() {
        if (!tableExists("semantic_tag") || !tableExists("semantic_tags")) {
            return;
        }

        boolean labelsUseLegacyTagTable = foreignKeyReferences("label", "semantic_tag_id", "semantic_tag");
        boolean linksUseLegacyTagTable = foreignKeyReferences("semantic_links", "semantic_tag_one_id", "semantic_tag")
                || foreignKeyReferences("semantic_links", "semantic_tag_two_id", "semantic_tag");

        if (!labelsUseLegacyTagTable && !linksUseLegacyTagTable) {
            return;
        }

        jdbcTemplate.update("""
                INSERT INTO semantic_tags (id, name)
                SELECT legacy_tag.id, legacy_tag.name
                FROM semantic_tag legacy_tag
                WHERE NOT EXISTS (
                    SELECT 1 FROM semantic_tags current_tag WHERE current_tag.id = legacy_tag.id
                )
                ON CONFLICT (name) DO NOTHING
                """);

        jdbcTemplate.update("""
                UPDATE label
                SET semantic_tag_id = current_tag.id
                FROM semantic_tag legacy_tag
                JOIN semantic_tags current_tag ON LOWER(current_tag.name) = LOWER(legacy_tag.name)
                WHERE label.semantic_tag_id = legacy_tag.id
                  AND label.semantic_tag_id <> current_tag.id
                """);

        jdbcTemplate.update("""
                UPDATE semantic_links
                SET semantic_tag_one_id = current_tag.id
                FROM semantic_tag legacy_tag
                JOIN semantic_tags current_tag ON LOWER(current_tag.name) = LOWER(legacy_tag.name)
                WHERE semantic_links.semantic_tag_one_id = legacy_tag.id
                  AND semantic_links.semantic_tag_one_id <> current_tag.id
                """);

        jdbcTemplate.update("""
                UPDATE semantic_links
                SET semantic_tag_two_id = current_tag.id
                FROM semantic_tag legacy_tag
                JOIN semantic_tags current_tag ON LOWER(current_tag.name) = LOWER(legacy_tag.name)
                WHERE semantic_links.semantic_tag_two_id = legacy_tag.id
                  AND semantic_links.semantic_tag_two_id <> current_tag.id
                """);

        jdbcTemplate.update("""
                UPDATE label
                SET semantic_tag_id = NULL
                WHERE semantic_tag_id IS NOT NULL
                  AND NOT EXISTS (
                      SELECT 1 FROM semantic_tags WHERE semantic_tags.id = label.semantic_tag_id
                  )
                """);

        jdbcTemplate.update("""
                DELETE FROM semantic_links
                WHERE NOT EXISTS (
                    SELECT 1 FROM semantic_tags WHERE semantic_tags.id = semantic_links.semantic_tag_one_id
                )
                   OR NOT EXISTS (
                    SELECT 1 FROM semantic_tags WHERE semantic_tags.id = semantic_links.semantic_tag_two_id
                )
                """);

        if (linksUseLegacyTagTable) {
            dropForeignKeys("semantic_links", "semantic_tag_one_id");
            dropForeignKeys("semantic_links", "semantic_tag_two_id");
            jdbcTemplate.execute("""
                    ALTER TABLE semantic_links
                    ADD CONSTRAINT fk_sem_link_tag_one FOREIGN KEY (semantic_tag_one_id)
                    REFERENCES semantic_tags (id) ON DELETE CASCADE
                    """);
            jdbcTemplate.execute("""
                    ALTER TABLE semantic_links
                    ADD CONSTRAINT fk_sem_link_tag_two FOREIGN KEY (semantic_tag_two_id)
                    REFERENCES semantic_tags (id) ON DELETE CASCADE
                    """);
        }

        if (labelsUseLegacyTagTable) {
            dropForeignKeys("label", "semantic_tag_id");
            jdbcTemplate.execute("""
                    ALTER TABLE label
                    ADD CONSTRAINT fk_label_semantic_tag FOREIGN KEY (semantic_tag_id)
                    REFERENCES semantic_tags (id) ON DELETE SET NULL
                    """);
        }

        jdbcTemplate.execute("""
                SELECT setval(pg_get_serial_sequence('semantic_tags', 'id'), COALESCE((SELECT MAX(id) FROM semantic_tags), 1), true)
                """);
    }

    private boolean tableExists(String tableName) {
        try (Connection connection = dataSource.getConnection();
             ResultSet tables = connection.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            if (tables.next()) {
                return true;
            }
        } catch (SQLException ignored) {
            return false;
        }

        try (Connection connection = dataSource.getConnection();
             ResultSet tables = connection.getMetaData().getTables(null, null, tableName.toUpperCase(), new String[]{"TABLE"})) {
            return tables.next();
        } catch (SQLException ignored) {
            return false;
        }
    }

    private boolean foreignKeyReferences(String tableName, String columnName, String referencedTableName) {
        try (Connection connection = dataSource.getConnection();
             ResultSet importedKeys = connection.getMetaData().getImportedKeys(null, null, tableName)) {
            while (importedKeys.next()) {
                if (columnName.equalsIgnoreCase(importedKeys.getString("FKCOLUMN_NAME"))
                        && referencedTableName.equalsIgnoreCase(importedKeys.getString("PKTABLE_NAME"))) {
                    return true;
                }
            }
        } catch (SQLException ignored) {
            return false;
        }

        try (Connection connection = dataSource.getConnection();
             ResultSet importedKeys = connection.getMetaData().getImportedKeys(null, null, tableName.toUpperCase())) {
            while (importedKeys.next()) {
                if (columnName.equalsIgnoreCase(importedKeys.getString("FKCOLUMN_NAME"))
                        && referencedTableName.equalsIgnoreCase(importedKeys.getString("PKTABLE_NAME"))) {
                    return true;
                }
            }
            return false;
        } catch (SQLException ignored) {
            return false;
        }
    }

    private void dropForeignKeys(String tableName, String columnName) {
        List<String> constraintNames = findForeignKeyConstraints(tableName, columnName);
        for (String constraintName : constraintNames) {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName);
        }
    }

    private List<String> findForeignKeyConstraints(String tableName, String columnName) {
        try (Connection connection = dataSource.getConnection();
             ResultSet importedKeys = connection.getMetaData().getImportedKeys(null, null, tableName)) {
            List<String> constraintNames = new ArrayList<>();
            while (importedKeys.next()) {
                if (columnName.equalsIgnoreCase(importedKeys.getString("FKCOLUMN_NAME"))) {
                    constraintNames.add(importedKeys.getString("FK_NAME"));
                }
            }
            return constraintNames;
        } catch (SQLException ignored) {
            return List.of();
        }
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

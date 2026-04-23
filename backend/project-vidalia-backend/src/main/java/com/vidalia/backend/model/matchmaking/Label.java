package com.vidalia.backend.model.matchmaking;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Optional semantic tag
    @ManyToOne
    @JoinColumn(name = "semantic_tag_id", nullable = true)
    private SemanticTag semanticTag;

    @Column(nullable = false)
    private boolean required;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LabelType type;

}

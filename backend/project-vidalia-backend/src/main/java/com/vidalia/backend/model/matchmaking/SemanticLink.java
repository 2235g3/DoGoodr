package com.vidalia.backend.model.matchmaking;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "semantic_links")
public class SemanticLink {
    @EmbeddedId
    private SemanticLinkId id;

    @ManyToOne
    @MapsId("semanticTagOneId")
    @JoinColumn(name = "semantic_tag_one_id")
    private SemanticTag tagOne;

    @ManyToOne
    @MapsId("semanticTagTwoId")
    @JoinColumn(name = "semantic_tag_two_id")
    private SemanticTag tagTwo;

    @Column(nullable = false)
    private double weight;

    public SemanticLink(SemanticTag tagOne, SemanticTag tagTwo, double weight) {
        this.tagOne = tagOne;
        this.tagTwo = tagTwo;
        this.id = new SemanticLinkId(tagOne.getId(), tagTwo.getId());
        this.weight = weight;
    }
}

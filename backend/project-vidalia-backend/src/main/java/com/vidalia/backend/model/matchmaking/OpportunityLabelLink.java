package com.vidalia.backend.model.matchmaking;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "opportunity_label_links")
public class OpportunityLabelLink {

    @EmbeddedId
    private OpportunityLabelKey id;

    @ManyToOne
    @MapsId("labelId")
    @JoinColumn(name = "label_id")
    private Label label;

    @Column(name = "opportunity_id", insertable = false, updatable = false)
    private UUID opportunityId;

    @Column(nullable = false)
    private double weight;

    public OpportunityLabelLink(UUID opportunityId, Label label, double weight) {
        this.id = new OpportunityLabelKey(opportunityId, label.getId());
        this.label = label;
        this.weight = weight;

    }

}
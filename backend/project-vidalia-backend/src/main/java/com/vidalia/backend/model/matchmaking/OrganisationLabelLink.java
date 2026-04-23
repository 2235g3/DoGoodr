package com.vidalia.backend.model.matchmaking;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "organisation_label_links")
public class OrganisationLabelLink {

    @EmbeddedId
    private OrganisationLabelKey id;

    @ManyToOne
    @MapsId("labelId")
    @JoinColumn(name = "label_id")
    private Label label;

    @Column(name = "organisation_id", insertable = false, updatable = false)
    private UUID organisationId;

    @Column(nullable = false)
    private double weight;

    public OrganisationLabelLink(UUID orgId, Label label, double weight) {
        this.id = new OrganisationLabelKey(orgId, label.getId());
        this.label = label;
        this.weight = weight;
    }
}

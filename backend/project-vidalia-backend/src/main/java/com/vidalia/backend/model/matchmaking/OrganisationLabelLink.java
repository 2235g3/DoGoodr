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
@Table(name = "organisation_label_links")
public class OrganisationLabelLink {

    @EmbeddedId
    private OrganisationLabelKey id;

    @ManyToOne
    @MapsId("labelId")
    @JoinColumn(name = "label_id")
    private Label label;

    // The composite key (OrganisationLabelKey) contains organisationId; avoid duplicating column mapping

    @Column(nullable = false)
    private double weight;

    public OrganisationLabelLink(UUID orgId, Label label, double weight) {
        this.id = new OrganisationLabelKey(orgId, label.getId());
        this.label = label;
        this.weight = weight;
    }

    public UUID getOrganisationId() {
        return this.id != null ? this.id.getOrganisationId() : null;
    }
}

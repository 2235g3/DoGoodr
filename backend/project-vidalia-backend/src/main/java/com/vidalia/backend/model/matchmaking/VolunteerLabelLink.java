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
@Table(name = "volunteer_label_links")
public class VolunteerLabelLink {

    @EmbeddedId
    private VolunteerLabelKey id;

    @ManyToOne
    @MapsId("labelId")
    @JoinColumn(name = "label_id")
    private Label label;

    @Column(name = "volunteer_id", insertable = false, updatable = false)
    private UUID volunteerId;

    @Column(nullable = false)
    private double weight;


    public VolunteerLabelLink(UUID volunteerId, Label label, double weight) {
        this.id = new VolunteerLabelKey(volunteerId, label.getId());
        this.label = label;
        this.weight = weight;
    }

}

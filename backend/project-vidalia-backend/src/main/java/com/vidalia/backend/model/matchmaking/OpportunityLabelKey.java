package com.vidalia.backend.model.matchmaking;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class OpportunityLabelKey implements Serializable {

    private UUID opportunityId;
    private Long labelId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OpportunityLabelKey that)) {
            return false;
        }
        return Objects.equals(opportunityId, that.opportunityId)
                && Objects.equals(labelId, that.labelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opportunityId, labelId);
    }
}


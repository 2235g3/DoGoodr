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
public class VolunteerLabelKey implements Serializable {

    private UUID volunteerId;
    private Long labelId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VolunteerLabelKey that)) {
            return false;
        }
        return Objects.equals(volunteerId, that.volunteerId)
                && Objects.equals(labelId, that.labelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(volunteerId, labelId);
    }

}
package com.vidalia.backend.model.matchmaking;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SemanticLinkId implements Serializable {
    private Long semanticTagOneId;
    private Long semanticTagTwoId;

    // equals & hashCode REQUIRED
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemanticLinkId)) return false;
        SemanticLinkId that = (SemanticLinkId) o;
        return Objects.equals(semanticTagOneId, that.semanticTagOneId) &&
                Objects.equals(semanticTagTwoId, that.semanticTagTwoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(semanticTagOneId, semanticTagTwoId);
    }
}

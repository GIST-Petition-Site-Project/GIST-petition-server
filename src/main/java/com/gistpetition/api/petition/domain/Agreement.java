package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.UnmodifiableEntity;
import lombok.Getter;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Getter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "petition_id"}))
public class Agreement extends UnmodifiableEntity {

    @Lob
    private String description;
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petition_id")
    private Petition petition;

    protected Agreement() {
    }

    public Agreement(String description, Long userId) {
        this(description, userId, null);
    }

    private Agreement(String description, Long userId, Petition petition) {
        this.description = description;
        this.userId = userId;
        this.petition = petition;
    }

    public boolean writtenBy(Long userId) {
        return this.userId.equals(userId);
    }

    public void setPetition(Petition petition, Instant at) {
        this.petition = petition;
        petition.addAgreement(this, at);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agreement agreement = (Agreement) o;
        return Objects.equals(userId, agreement.userId) && Objects.equals(petition, agreement.petition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, petition);
    }
}

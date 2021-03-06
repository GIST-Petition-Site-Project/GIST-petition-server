package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.UnmodifiableEntity;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "petition_id"}))
public class Agreement extends UnmodifiableEntity {

    @Lob
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "user_id")
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petition_id", referencedColumnName = "id")
    private Petition petition;

    protected Agreement() {
    }

    public Agreement(String description, Long userId) {
        this(description, userId, null);
    }

    public Agreement(String description, Long userId, Petition petition) {
        this.description = description;
        this.userId = userId;
        this.petition = petition;
    }

    public boolean writtenBy(Long userId) {
        return this.userId.equals(userId);
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

package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.UnmodifiableEntity;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Agreement extends UnmodifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String description;
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "petition_id")
    private Petition petition;

    protected Agreement() {
    }

    public Agreement(Long userId, String description, Petition petition) {
        this(null, description, userId, petition);
    }

    private Agreement(Long id, String description, Long userId, Petition petition) {
        this.id = id;
        this.description = description;
        this.userId = userId;
        this.petition = petition;
    }

    public boolean isAgreedBy(Long userId) {
        return this.userId.equals(userId);
    }
}

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petition_id")
    private Petition petition;

    protected Agreement() {
    }

    public Agreement(String description, Long userId) {
        this(null, description, userId, null);
    }

    public Agreement(String description, Long userId, Petition petition) {
        this(null, description, userId, petition);
    }


    private Agreement(Long id, String description, Long userId, Petition petition) {
        this.id = id;
        this.description = description;
        this.userId = userId;
        this.petition = petition;
    }


    public boolean writtenBy(Long userId) {
        return this.userId.equals(userId);
    }

    public void setPetition(Petition petition) {
        this.petition = petition;
        petition.addAgreement(this);
    }
}

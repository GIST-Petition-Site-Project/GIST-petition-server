package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rejection extends BaseEntity {
    @Embedded
    private Description description;
    @OneToOne(mappedBy = "rejection")
    private Petition petition;

    public Rejection(String description, Petition petition) {
        this.description = new Description(description);
        this.petition = petition;
    }

    public String getDescription() {
        return description.getDescription();
    }

    public void update(String description) {
        this.description.update(description);
    }
}

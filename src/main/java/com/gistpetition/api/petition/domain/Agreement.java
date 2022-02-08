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

    protected Agreement() {
    }

    public Agreement(Long userId, String description) {
        this(null, description, userId);
    }

    private Agreement(Long id, String description, Long userId) {
        this.id = id;
        this.description = description;
        this.userId = userId;
    }

    public boolean isAgreedBy(Long userId) {
        return this.userId.equals(userId);
    }
}

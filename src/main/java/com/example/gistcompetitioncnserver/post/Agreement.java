package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.common.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Agreement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    protected Agreement() {
    }

    public Agreement(Long userId) {
        this(null, userId);
    }

    private Agreement(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public boolean isAgreedBy(Long userId) {
        return this.userId.equals(userId);
    }

    public Long getUserId() {
        return userId;
    }
}

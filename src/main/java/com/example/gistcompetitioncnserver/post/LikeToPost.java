package com.example.gistcompetitioncnserver.post;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LikeToPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    protected LikeToPost() {
    }

    public LikeToPost(Long userId) {
        this(null, userId);
    }

    private LikeToPost(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public boolean isLikedBy(Long userId) {
        return this.userId.equals(userId);
    }

    public Long getUserId() {
        return userId;
    }
}

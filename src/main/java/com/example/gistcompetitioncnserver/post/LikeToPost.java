package com.example.gistcompetitioncnserver.post;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LikeToPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likeId")
    private Long likeId;

    private Long userId;

    protected LikeToPost() {
    }

    public LikeToPost(Long userId) {
        this(null, userId);
    }

    private LikeToPost(Long likeId, Long userId) {
        this.likeId = likeId;
        this.userId = userId;
    }

    public boolean isLikedBy(Long userId) {
        return this.userId.equals(userId);
    }

    public Long getUserId() {
        return userId;
    }
}

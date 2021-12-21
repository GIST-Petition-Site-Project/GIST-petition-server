package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.common.BaseEntity;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Long postId;
    private Long userId;

    protected Comment() {
    }

    public Comment(String content, Long postId, Long userId) {
        this(null, content, postId, userId);
    }

    public Comment(Long id, String content, Long postId, Long userId) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
    }

    public void validate(CommentValidator commentValidator) {
        commentValidator.validate(this);
    }

    public void updateContent(String changedContent) {
        this.content = changedContent;
    }
}

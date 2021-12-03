package com.example.gistcompetitioncnserver.comment;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Long postId;
    private Long userId;
    private LocalDateTime created;

    protected Comment() {
    }

    public Comment(String content, Long postId, Long userId) {
        this(null, content, postId, userId, LocalDateTime.now());
    }

    public Comment(Long id, String content, Long postId, Long userId, LocalDateTime created) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
        this.created = created;
    }

    public void validate(CommentValidator commentValidator) {
        commentValidator.validate(this);
    }

    public void updateContent(String changedContent) {
        this.content = changedContent;
    }
}

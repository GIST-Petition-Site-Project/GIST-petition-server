package com.example.gistcompetitioncnserver.comment.domain;

import com.example.gistcompetitioncnserver.common.persistence.BaseEntity;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
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

    public void updateContent(String changedContent) {
        this.content = changedContent;
    }
}

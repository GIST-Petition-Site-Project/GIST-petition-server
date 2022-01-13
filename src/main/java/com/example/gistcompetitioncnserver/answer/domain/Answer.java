package com.example.gistcompetitioncnserver.answer.domain;

import com.example.gistcompetitioncnserver.common.persistence.BaseEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String content;
    private Long postId;
    private Long userId;

    protected Answer() {
    }

    public Answer(String content, Long postId, Long userId) {
        this(null, content, postId, userId);
    }

    public Answer(Long id, String content, Long postId, Long userId) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}

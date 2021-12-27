package com.example.gistcompetitioncnserver.answer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime created;
    private Long postId;
    private Long userId;

    protected Answer() {
    }

    public Answer(String content, Long postId, Long userId) {
        this(null, content, LocalDateTime.now(), postId, userId);
    }

    public Answer(Long id, String content, LocalDateTime created, Long postId, Long userId) {
        this.id = id;
        this.content = content;
        this.created = created;
        this.postId = postId;
        this.userId = userId;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}

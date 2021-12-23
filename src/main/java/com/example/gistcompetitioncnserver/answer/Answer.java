package com.example.gistcompetitioncnserver.answer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private String created;
    private Long postId;
    private Long userId;

    protected Answer() {
    }

    public Answer(Long id, String content, String created, Long postId, Long userId) {
        this.id = id;
        this.content = content;
        this.created = created;
        this.postId = postId;
        this.userId = userId;
    }
}

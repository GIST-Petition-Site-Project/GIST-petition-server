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

    private String title;

    private String description;

    private String category;

    private String created;

    private Long userId;

    protected Answer() {
    }

    public Answer(Long id, String title, String description, String category, String created, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.created = created;
        this.userId = userId;
    }
}

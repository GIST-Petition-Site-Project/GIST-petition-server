package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String category;

    private Long userId;

    protected Answer() {
    }

    public Answer(Long id, String title, String description, String category, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.userId = userId;
    }
}

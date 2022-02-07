package com.gistpetition.api.answer.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import lombok.Getter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Entity
@Getter
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String content;
    private Long petitionId;
    private Long userId;

    protected Answer() {
    }

    public Answer(String content, Long petitionId, Long userId) {
        this(null, content, petitionId, userId);
    }

    public Answer(Long id, String content, Long petitionId, Long userId) {
        this.id = id;
        this.content = content;
        this.petitionId = petitionId;
        this.userId = userId;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}

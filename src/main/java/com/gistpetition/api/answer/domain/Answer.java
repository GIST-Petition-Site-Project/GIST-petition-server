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

    protected Answer() {
    }

    public Answer(String content, Long petitionId) {
        this(null, content, petitionId);
    }

    public Answer(Long id, String content, Long petitionId) {
        this.id = id;
        this.content = content;
        this.petitionId = petitionId;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}

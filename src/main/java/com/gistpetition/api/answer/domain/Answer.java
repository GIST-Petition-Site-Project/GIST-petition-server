package com.gistpetition.api.answer.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import lombok.Getter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Audited
@Entity
@Getter
public class Answer extends BaseEntity {
    @Lob
    private String content;
    @Column(unique = true)
    private Long petitionId;

    protected Answer() {
    }

    public Answer(String content, Long petitionId) {
        this.content = content;
        this.petitionId = petitionId;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}

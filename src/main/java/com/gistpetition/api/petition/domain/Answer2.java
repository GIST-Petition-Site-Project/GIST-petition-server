package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer2 extends BaseEntity {
    private String content;
    @OneToOne
    @JoinColumn(name = "petition_id", referencedColumnName = "id", unique = true)
    private Petition petition;

    public Answer2(String content, Petition petition) {
        this.content = content;
        this.petition = petition;
    }

    public void updateContent(String updateAnswerContent) {
        this.content = updateAnswerContent;
    }
}

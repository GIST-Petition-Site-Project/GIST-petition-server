package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

@Entity
@Getter
@Audited
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseEntity {
    @Lob
    @Column(name = "content", nullable = false)
    private String content;
    @NotAudited
    @OneToOne(mappedBy = "answer")
    private Petition petition;

    public Answer(String content, Petition petition) {
        this.content = content;
        this.petition = petition;
    }

    public void updateContent(String updateAnswerContent) {
        this.content = updateAnswerContent;
    }
}

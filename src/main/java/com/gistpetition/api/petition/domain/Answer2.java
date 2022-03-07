package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import lombok.Getter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

@Audited
@Entity
@Getter
public class Answer2 extends BaseEntity {
    @Lob
    private String content;
    @OneToOne
    @JoinColumn(name = "petition_id", referencedColumnName = "id", unique = true)
    private Petition petition;

    protected Answer2() {
    }

    public Answer2(String content, Petition petition) {
        this.content = content;
        this.petition = petition;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void detach() {
        this.petition = null;
    }
}

package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Getter
@Audited
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseEntity {
    @Embedded
    private Description description;
    @Embedded
    private VideoUrl videoUrl;
    @NotAudited
    @OneToOne(mappedBy = "answer")
    private Petition petition;

    public Answer(String description, VideoUrl videoUrl, Petition petition) {
        this.description = new Description(description);
        this.videoUrl = videoUrl;
        this.petition = petition;
    }

    public void update(String description) {
        this.description.update(description);
    }

    public String getDescription() {
        return this.description.getDescription();
    }
}

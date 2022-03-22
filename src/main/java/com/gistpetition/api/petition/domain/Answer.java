package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Column;
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
    @Column(name = "video_url")
    private String videoUrl;
    @NotAudited
    @OneToOne(mappedBy = "answer")
    private Petition petition;

    public Answer(String description, Petition petition) {
        this(description, null, petition);
    }

    public Answer(String description, String videoUrl, Petition petition) {
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

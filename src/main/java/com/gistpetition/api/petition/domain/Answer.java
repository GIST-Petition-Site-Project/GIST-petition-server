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
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "video_url")
    private String videoUrl;
    @NotAudited
    @OneToOne(mappedBy = "answer")
    private Petition petition;

    public Answer(String description, Petition petition) {
        this(description, null, petition);
    }

    public Answer(String description, String videoUrl, Petition petition) {
        this.description = description;
        this.videoUrl = videoUrl;
        this.petition = petition;
    }

    public void update(String description) {
        this.description = description;
    }
}

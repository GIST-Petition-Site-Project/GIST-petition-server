package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import com.gistpetition.api.utils.urlmatcher.UrlMatcher;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
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

    public void update(String description, String videoUrl, UrlMatcher urlMatcher) {
        this.description.update(description);
        this.videoUrl.update(videoUrl, urlMatcher);
    }

    public String getDescription() {
        return this.description.getDescription();
    }

    public String getVideoUrl() {
        return videoUrl.getVideoUrl();
    }
}

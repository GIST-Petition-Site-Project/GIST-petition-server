package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.NotMatchedVideoUrlPatternException;
import com.gistpetition.api.utils.urlmatcher.UrlMatcher;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoUrl {

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    public VideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void update(String videoUrl, UrlMatcher urlMatcher) {
        if (Objects.isNull(videoUrl) || videoUrl.isBlank()) {
            this.videoUrl = "";
            return;
        }
        if (!urlMatcher.isMatched(videoUrl)) {
            throw new NotMatchedVideoUrlPatternException();
        }
        this.videoUrl = videoUrl;
    }

    public static VideoUrl of(String url, UrlMatcher urlMatcher) {
        if (Objects.isNull(url) || url.isBlank()) {
            return new VideoUrl("");
        }
        if (!urlMatcher.isMatched(url)) {
            throw new NotMatchedVideoUrlPatternException();
        }
        return new VideoUrl(url);
    }
}

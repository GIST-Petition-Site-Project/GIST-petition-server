package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.NotYoutubeUrlPatternException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoUrl {
    private static final Pattern youtubePattern = Pattern.compile("^(http(s)?://)?((w){3}.)?youtu(be|.be)?(.com)?/.+");

    @Column(name = "video_url")
    private String videoUrl;

    public VideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public static VideoUrl ofYoutube(String videoUrl) {
        if (Objects.isNull(videoUrl)) {
            return null;
        }
        if (!youtubePattern.matcher(videoUrl).matches()) {
            throw new NotYoutubeUrlPatternException();
        }
        return new VideoUrl(videoUrl);
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}

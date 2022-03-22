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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void update(String videoUrl) {
        if (Objects.isNull(videoUrl) || videoUrl.isBlank()) {
            this.videoUrl = "";
            return;
        }
        if (!youtubePattern.matcher(videoUrl).matches()) {
            throw new NotYoutubeUrlPatternException();
        }
        this.videoUrl = videoUrl;
    }

    public static VideoUrl ofYoutube(String url) {
        VideoUrl videourl = new VideoUrl();

        videourl.update(url);

        return videourl;
    }
}

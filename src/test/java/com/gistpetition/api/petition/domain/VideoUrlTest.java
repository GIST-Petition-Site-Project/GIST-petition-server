package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.NotYoutubeUrlPatternException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VideoUrlTest {

    public static final String VALID_YOUTUBE_URL = "https://www.youtube.com/watch?v=XbL-AwYX8ME";

    @ParameterizedTest
    @ValueSource(strings = {
            "http://youtu.be/t-ZRX8984sc",
            "http://youtube.com/watch?v=iwGFalTRHDA",
            "http://www.youtube.com/watch?v=t-ZRX8984sc",
            "youtube.com/n17B_uFF4cA",
            "http://youtu.be/n17B_uFF4cA"})
    void youtubePattern(String url) {
        VideoUrl videoUrl = VideoUrl.ofYoutube(url);
        assertThat(videoUrl).isNotNull();
        assertThat(videoUrl.getVideoUrl()).isEqualTo(url);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ttp://youtu.be/t-ZRX8984sc",
            "p://youtube.com/watch?v=iwGFalTRHDA",
            "http://www.gist-petition.com/",
            "https://youtube.co"})
    void invalid_youtubePattern(String url) {
        assertThatThrownBy(() -> VideoUrl.ofYoutube(url)).isInstanceOf(NotYoutubeUrlPatternException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void videoUrl_if_blank(String string) {
        assertThat(VideoUrl.ofYoutube(string).getVideoUrl()).isEqualTo("");
    }

    @Test
    void videoUrl_if_null() {
        assertThat(VideoUrl.ofYoutube(null).getVideoUrl()).isEqualTo("");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://youtu.be/t-ZRX8984sc",
            "http://youtube.com/watch?v=iwGFalTRHDA",
            "http://www.youtube.com/watch?v=t-ZRX8984sc",
            "youtube.com/n17B_uFF4cA",
            "http://youtu.be/n17B_uFF4cA"})
    void update_videoUrl(String url) {
        VideoUrl videoUrl = new VideoUrl(VALID_YOUTUBE_URL);

        videoUrl.update(url);

        assertThat(videoUrl.getVideoUrl()).isEqualTo(url);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ttp://youtu.be/t-ZRX8984sc",
            "p://youtube.com/watch?v=iwGFalTRHDA",
            "http://www.gist-petition.com/",
            "https://youtube.co"})
    void update_invalid_youtubePattern(String url) {
        VideoUrl videoUrl = new VideoUrl(VALID_YOUTUBE_URL);

        assertThatThrownBy(() -> videoUrl.update(url)).isInstanceOf(NotYoutubeUrlPatternException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void update_videoUrl_if_blank(String string) {
        VideoUrl videoUrl = new VideoUrl(VALID_YOUTUBE_URL);

        videoUrl.update(string);

        assertThat(videoUrl.getVideoUrl()).isEqualTo("");
    }

    @Test
    void update_videoUrl_if_null() {
        VideoUrl videoUrl = new VideoUrl(VALID_YOUTUBE_URL);

        videoUrl.update(null);

        assertThat(videoUrl.getVideoUrl()).isEqualTo("");
    }
}
package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.NotMatchedVideoUrlPatternException;
import com.gistpetition.api.utils.urlmatcher.UrlMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VideoUrlTest {
    private static final String URL = "www.gist-petition.com";
    private static final UrlMatcher ALWAYS_TRUE_URL_MATCHER = input -> true;
    private static final UrlMatcher ALWAYS_FALSE_URL_MATCHER = input -> false;

    @Test
    void youtubePattern() {
        VideoUrl videoUrl = VideoUrl.of(URL, ALWAYS_TRUE_URL_MATCHER);
        assertThat(videoUrl).isNotNull();
        assertThat(videoUrl.getVideoUrl()).isEqualTo(URL);
    }

    @Test
    void invalid_youtubePattern() {
        assertThatThrownBy(() -> VideoUrl.of(URL, ALWAYS_FALSE_URL_MATCHER))
                .isInstanceOf(NotMatchedVideoUrlPatternException.class);
    }

    @Test
    void videoUrl_if_null() {
        assertThat(VideoUrl.of(null, ALWAYS_TRUE_URL_MATCHER).getVideoUrl()).isEqualTo("");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void videoUrl_if_blank(String string) {
        assertThat(VideoUrl.of(string, ALWAYS_TRUE_URL_MATCHER).getVideoUrl()).isEqualTo("");
    }


    @Test
    void update_videoUrl() {
        VideoUrl videoUrl = new VideoUrl(URL);

        String updateUrl = URL + "update";
        videoUrl.update(updateUrl, ALWAYS_TRUE_URL_MATCHER);

        assertThat(videoUrl.getVideoUrl()).isEqualTo(updateUrl);
    }

    @Test
    void update_invalid_youtubePattern() {
        VideoUrl videoUrl = new VideoUrl(URL);

        assertThatThrownBy(() -> videoUrl.update(URL + "update", ALWAYS_FALSE_URL_MATCHER))
                .isInstanceOf(NotMatchedVideoUrlPatternException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void update_videoUrl_if_blank(String url) {
        VideoUrl videoUrl = new VideoUrl(URL);

        videoUrl.update(url, ALWAYS_TRUE_URL_MATCHER);

        assertThat(videoUrl.getVideoUrl()).isEqualTo("");
    }

    @Test
    void update_videoUrl_if_null() {
        VideoUrl videoUrl = new VideoUrl(URL);

        videoUrl.update(null, ALWAYS_TRUE_URL_MATCHER);

        assertThat(videoUrl.getVideoUrl()).isEqualTo("");
    }
}
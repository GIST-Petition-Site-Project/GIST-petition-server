package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.NotYoutubeUrlPatternException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VideoUrlTest {

    @ParameterizedTest
    @ValueSource(strings = {"http://youtu.be/t-ZRX8984sc",
            "http://youtube.com/watch?v=iwGFalTRHDA",
            "http://www.youtube.com/watch?v=t-ZRX8984sc",
            "http://www.youtube.com/watch?v=iwGFalTRHDA&feature=related",
            "http://www.youtube.com/embed/watch?feature=player_embedded&v=r5nB9u4jjy4",
            "https://www.youtube.com/channel/UCDZkgJZDyUnqwB070OyP72g",
            "youtube.com/n17B_uFF4cA",
            "youtube.com/iwGFalTRHDA",
            "http://youtu.be/n17B_uFF4cA",
            "https://youtube.com/iwGFalTRHDA",
            "https://youtube.com/channel/UCDZkgJZDyUnqwB070OyP72g"})
    void youtubePattern(String url) {
        VideoUrl videoUrl = VideoUrl.ofYoutube(url);
        assertThat(videoUrl).isNotNull();
        assertThat(videoUrl.getVideoUrl()).isEqualTo(url);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ttp://youtu.be/t-ZRX8984sc",
            "p://youtube.com/watch?v=iwGFalTRHDA",
            "http://www.naver.com/watch?v=t-ZRX8984sc",
            "http://www.kakao.com/watch?v=iwGFalTRHDA&feature=related",
            "http://www.gist-petition.com/",
            "https://www.gist.ac.kr",
            "https://youtube.co"})
    void invalid_youtubePattern(String url) {
        assertThatThrownBy(() -> VideoUrl.ofYoutube(url)).isInstanceOf(NotYoutubeUrlPatternException.class);
    }

    @Test
    void videoUrl_if_null() {
        assertThat(VideoUrl.ofYoutube(null)).isNull();
    }
}
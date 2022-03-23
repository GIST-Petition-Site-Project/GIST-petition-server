package com.gistpetition.api.utils.urlmatcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YoutubeUrlMatcherTest {

    private YoutubeUrlMatcher urlMatcher;

    @BeforeEach
    void setUp() {
        urlMatcher = new YoutubeUrlMatcher();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://youtu.be/t-ZRX8984sc",
            "http://youtube.com/watch?v=iwGFalTRHDA",
            "http://www.youtube.com/watch?v=t-ZRX8984sc",
            "youtube.com/n17B_uFF4cA",
            "http://youtu.be/n17B_uFF4cA"})
    void youtube_url_matches(String url) {
        assertTrue(urlMatcher.isMatched(url));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ttp://youtu.be/t-ZRX8984sc",
            "p://youtube.com/watch?v=iwGFalTRHDA",
            "http://www.gist-petition.com/",
            "https://youtube.co"})
    void youtube_url_unmatched(String url) {
        assertFalse(urlMatcher.isMatched(url));
    }
}
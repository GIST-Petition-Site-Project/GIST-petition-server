package com.gistpetition.api.utils.urlmatcher;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class YoutubeUrlMatcher implements UrlMatcher {
    private static final Pattern youtubePattern = Pattern.compile("^(http(s)?://)?((w){3}.)?youtu(be|.be)?(.com)?/.+");

    @Override
    public boolean isMatched(String url) {
        return youtubePattern.matcher(url).matches();
    }
}

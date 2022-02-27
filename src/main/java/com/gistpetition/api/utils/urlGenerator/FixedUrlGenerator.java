package com.gistpetition.api.utils.urlGenerator;

public class FixedUrlGenerator implements UrlGenerator {
    public static final String URL_CHAR = "A";

    @Override
    public String generate(int urlLength) {
        return URL_CHAR.repeat(urlLength);
    }
}
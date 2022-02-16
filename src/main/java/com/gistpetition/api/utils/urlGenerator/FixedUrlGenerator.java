package com.gistpetition.api.utils.urlGenerator;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!dev && !prod")
@Component
public class FixedUrlGenerator implements UrlGenerator {
    public static final String FIXED_URL = "AAAAAA";

    @Override
    public String generate(int urlLength) {
        return FIXED_URL;
    }
}

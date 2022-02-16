package com.gistpetition.api.utils.urlGenerator;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("dev || prod")
@Component
public class RandomUrlGenerator implements UrlGenerator {
    private static final char[] keys = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private final Random random = new Random();

    @Override
    public String generate(int urlLength) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < urlLength; i++) {
            sb.append(keys[random.nextInt(keys.length)]);
        }
        return sb.toString();
    }
}

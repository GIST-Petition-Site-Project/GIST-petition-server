package com.gistpetition.api.verification.application;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("dev || prod")
@Component
public class RandomVerificationCodeGenerator implements VerificationCodeGenerator {
    private final Random random = new Random();
    private static final int LENGTH = 6;
    private static final int LEFTMOST = 'A';
    private static final int RIGHTMOST = 'Z';

    @Override
    public String generate() {
        return random.ints(LEFTMOST, RIGHTMOST + 1)
                .limit(LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}

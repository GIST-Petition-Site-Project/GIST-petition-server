package com.gistpetition.api.verification.application;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomVerificationCodeGenerator implements VerificationCodeGenerator {
    @Override
    public String generate() {
        int length = 6;
        int leftMost = 'A';
        int rightMost = 'Z';

        Random random = new Random();

        return random.ints(leftMost, rightMost + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}

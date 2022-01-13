package com.gistpetition.api.verification.application;

import org.springframework.stereotype.Component;

@Component
public class VerificationCodeGeneratorImpl implements VerificationCodeGenerator {
    @Override
    public String generate() {
        return "AAAAAA";
    }
}

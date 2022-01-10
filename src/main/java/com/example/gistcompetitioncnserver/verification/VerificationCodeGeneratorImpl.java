package com.example.gistcompetitioncnserver.verification;

import org.springframework.stereotype.Component;

@Component
public class VerificationCodeGeneratorImpl implements VerificationCodeGenerator {
    @Override
    public String generate() {
        return "AAAAAA";
    }
}

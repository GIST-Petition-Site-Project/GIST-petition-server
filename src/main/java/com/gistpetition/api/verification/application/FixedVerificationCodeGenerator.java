package com.gistpetition.api.verification.application;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("!dev && !prod")
@Component
public class FixedVerificationCodeGenerator implements VerificationCodeGenerator {
    @Override
    public String generate() {
        return "AAAAAA";
    }
}

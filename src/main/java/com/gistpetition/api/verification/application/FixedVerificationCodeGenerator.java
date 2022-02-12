package com.gistpetition.api.verification.application;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!dev && !prod")
@Component
public class FixedVerificationCodeGenerator implements VerificationCodeGenerator {
    public static final String FIXED_VERIFICATION_CODE = "AAAAAA";

    @Override
    public String generate() {
        return FIXED_VERIFICATION_CODE;
    }
}

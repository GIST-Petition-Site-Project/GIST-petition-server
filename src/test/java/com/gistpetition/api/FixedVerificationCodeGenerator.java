package com.gistpetition.api;

import com.gistpetition.api.verification.application.VerificationCodeGenerator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("!dev && !prod")
@Component
public class FixedVerificationCodeGenerator implements VerificationCodeGenerator {
    public static final String FIXED_VERIFICATION_CODE = "AAAAAA";
    @Override
    public String generate() {
        return FIXED_VERIFICATION_CODE;
    }
}

package com.example.gistcompetitioncnserver.verification;

import org.springframework.stereotype.Component;

@Component
public class TokenGeneratorImpl implements TokenGenerator {
    @Override
    public String createToken() {
        return "AAAAAA";
    }
}

package com.example.gistcompetitioncnserver.verification;

import org.springframework.stereotype.Component;

public interface TokenGenerator {
    String createToken();
}

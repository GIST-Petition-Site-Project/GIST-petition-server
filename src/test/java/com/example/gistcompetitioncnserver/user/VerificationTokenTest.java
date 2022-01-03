package com.example.gistcompetitioncnserver.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VerificationTokenTest {
    private static final LocalDateTime TIME = LocalDateTime.of(2020, 1, 11, 0, 0);

    @Test
    void isValidToken() {
        VerificationToken validToken = new VerificationToken("token", 1L, TIME.plusMinutes(20));

        assertTrue(validToken.isValidAt(TIME));
    }

    @Test
    void isExpiredToken() {
        VerificationToken expiredToken = new VerificationToken("token", 1L, TIME.minusMinutes(20));

        assertFalse(expiredToken.isValidAt(TIME));
    }
}
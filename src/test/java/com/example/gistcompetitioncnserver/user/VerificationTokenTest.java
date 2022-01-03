package com.example.gistcompetitioncnserver.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VerificationTokenTest {
    private static final User USER = new User("username", "password", UserRole.USER);
    private static final LocalDateTime TIME = LocalDateTime.of(2020, 1, 11, 0, 0);

    @Test
    void isValidToken() {
        VerificationToken validToken = new VerificationToken(1L, "token", USER, TIME.plusMinutes(20));

        assertTrue(validToken.isValidAt(TIME));
    }

    @Test
    void isExpiredToken() {
        VerificationToken expiredToken = new VerificationToken(1L, "token", USER, TIME.minusMinutes(20));

        assertFalse(expiredToken.isValidAt(TIME));
    }
}
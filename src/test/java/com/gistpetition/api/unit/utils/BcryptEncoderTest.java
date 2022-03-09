package com.gistpetition.api.unit.utils;

import com.gistpetition.api.utils.password.BcryptEncoder;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BcryptEncoderTest {

    private final BcryptEncoder encoder = new BcryptEncoder();

    @Test
    void passwordIsHashedByBCrypt() {
        String password = "test-password";
        String hashed = encoder.hashPassword(password);

        assertThat(hashed).hasSize(60);
        assertTrue(hashed.startsWith("$2a$10$"));
    }

    @Test
    void isMatch() {
        String password = "test-password";
        assertTrue(encoder.isMatch(password, BCrypt.hashpw(password, BCrypt.gensalt())));
    }

    @Test
    void isMatchFailed() {
        String own = "own-password";
        String other = "other-password";
        assertFalse(encoder.isMatch(other, BCrypt.hashpw(own, BCrypt.gensalt())));
    }
}

package com.example.gistcompetitioncnserver.common.password;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BcryptEncoder implements Encoder {
    @Override
    public String hashPassword(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt());
    }

    @Override
    public boolean isMatch(String raw, String hashed) {
        return BCrypt.checkpw(raw, hashed);
    }
}

package com.example.gistcompetitioncnserver.user;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BcryptEncoder implements Encryptor {
    @Override
    public String hashPassword(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt());
    }
    @Override
    public boolean isMatch(String raw, String hashed) {
        return BCrypt.checkpw(raw, hashed);
    }
}

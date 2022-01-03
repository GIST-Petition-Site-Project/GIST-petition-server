package com.example.gistcompetitioncnserver.user;

public interface Encryptor {
    String hashPassword(String raw);

    boolean isMatch(String raw, String hashed);
}

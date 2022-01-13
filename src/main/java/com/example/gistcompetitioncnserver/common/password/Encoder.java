package com.example.gistcompetitioncnserver.common.password;

public interface Encoder {
    String hashPassword(String raw);

    boolean isMatch(String raw, String hashed);
}

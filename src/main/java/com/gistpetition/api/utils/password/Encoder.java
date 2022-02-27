package com.gistpetition.api.utils.password;

public interface Encoder {
    String hashPassword(String raw);

    boolean isMatch(String raw, String hashed);
}

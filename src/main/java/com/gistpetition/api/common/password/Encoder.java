package com.gistpetition.api.common.password;

public interface Encoder {
    String hashPassword(String raw);

    boolean isMatch(String raw, String hashed);
}

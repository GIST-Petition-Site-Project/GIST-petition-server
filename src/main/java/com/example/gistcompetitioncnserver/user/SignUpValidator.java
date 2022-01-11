package com.example.gistcompetitioncnserver.user;

public interface SignUpValidator {
    void checkIsVerified(String username, String verificationCode);
}

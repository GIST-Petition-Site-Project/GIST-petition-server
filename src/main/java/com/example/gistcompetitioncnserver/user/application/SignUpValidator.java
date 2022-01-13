package com.example.gistcompetitioncnserver.user.application;

public interface SignUpValidator {
    void checkIsVerified(String username, String verificationCode);
}

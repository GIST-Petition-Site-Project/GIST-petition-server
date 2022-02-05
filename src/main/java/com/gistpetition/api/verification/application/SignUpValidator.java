package com.gistpetition.api.verification.application;

public interface SignUpValidator {
    void checkIsVerified(String username, String verificationCode);
}

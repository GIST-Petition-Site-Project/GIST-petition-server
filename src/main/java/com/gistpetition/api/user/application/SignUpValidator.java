package com.gistpetition.api.user.application;

public interface SignUpValidator {
    void checkIsVerified(String username, String verificationCode);
}

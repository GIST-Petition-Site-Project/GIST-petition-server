package com.gistpetition.api.verification.application.password;

public interface FindPasswordValidator {
    void checkIsVerified(String username, String verificationCode);
}

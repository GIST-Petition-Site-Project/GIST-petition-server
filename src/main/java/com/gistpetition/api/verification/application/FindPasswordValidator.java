package com.gistpetition.api.verification.application;

public interface FindPasswordValidator {
    void checkIsVerified(String username, String verificationCode);
}

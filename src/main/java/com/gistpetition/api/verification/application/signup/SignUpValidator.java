package com.gistpetition.api.verification.application.signup;

public interface SignUpValidator {
    void checkIsVerified(String username, String verificationCode);
}

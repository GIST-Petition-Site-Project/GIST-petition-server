package com.example.gistcompetitioncnserver.user;

import org.springframework.stereotype.Component;

public interface SignUpValidator {
    void checkIsVerified(String username, String verificationCode);
}

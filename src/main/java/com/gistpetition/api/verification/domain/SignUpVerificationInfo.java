package com.gistpetition.api.verification.domain;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class SignUpVerificationInfo extends VerificationInfo {

    public SignUpVerificationInfo() {
    }

    public SignUpVerificationInfo(String username, String verificationCode) {
        super(username, verificationCode);
    }

    public SignUpVerificationInfo(Long id, String username, String verificationCode, LocalDateTime createdAt, LocalDateTime confirmedAt) {
        super(id, username, verificationCode, createdAt, confirmedAt);
    }
}

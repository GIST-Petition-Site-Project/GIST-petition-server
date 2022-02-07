package com.gistpetition.api.verification.domain;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class PasswordVerificationInfo extends VerificationInfo {
    public PasswordVerificationInfo() {
    }

    public PasswordVerificationInfo(String username, String verificationCode) {
        super(username, verificationCode);
    }

    public PasswordVerificationInfo(Long id, String username, String verificationCode, LocalDateTime createdAt, LocalDateTime confirmedAt) {
        super(id, username, verificationCode, createdAt, confirmedAt);
    }
}

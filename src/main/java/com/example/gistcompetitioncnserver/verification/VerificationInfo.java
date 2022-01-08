package com.example.gistcompetitioncnserver.verification;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
public class VerificationInfo {
    private static final int EXPIRE_MINUTE = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String verificationCode;
    private Boolean confirmed;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;

    public VerificationInfo() {
    }

    public VerificationInfo(String username, String verificationCode) {
        this(null, username, verificationCode, false, LocalDateTime.now(), null);
    }

    private VerificationInfo(Long id, String username, String verificationCode, Boolean confirmed, LocalDateTime createdAt, LocalDateTime confirmedAt) {
        this.id = id;
        this.username = username;
        this.verificationCode = verificationCode;
        this.confirmed = confirmed;
        this.createdAt = createdAt;
        this.confirmedAt = confirmedAt;
    }

    public boolean isExpiredAt(LocalDateTime time) {
        return time.isAfter(createdAt) && time.isBefore(createdAt.plusMinutes(EXPIRE_MINUTE));
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void confirm() {
        confirmed = true;
        confirmedAt = LocalDateTime.now();
    }
}

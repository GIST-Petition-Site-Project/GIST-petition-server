package com.example.gistcompetitioncnserver.verification;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
public class VerificationInfo {
    public static final int EXPIRE_MINUTE = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String verificationCode;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;

    public VerificationInfo() {
    }

    public VerificationInfo(String username, String verificationCode) {
        this(null, username, verificationCode, LocalDateTime.now(), null);
    }

    public VerificationInfo(Long id, String username, String verificationCode, LocalDateTime createdAt, LocalDateTime confirmedAt) {
        this.id = id;
        this.username = username;
        this.verificationCode = verificationCode;
        this.createdAt = createdAt;
        this.confirmedAt = confirmedAt;
    }

    public boolean isValidToConfirm(LocalDateTime time) {
        return time.isAfter(createdAt) && time.isBefore(createdAt.plusMinutes(EXPIRE_MINUTE));
    }

    public boolean isConfirmed() {
        return Objects.nonNull(confirmedAt);
    }

    public void confirm() {
        confirmedAt = LocalDateTime.now();
    }
}

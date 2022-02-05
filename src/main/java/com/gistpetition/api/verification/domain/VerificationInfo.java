package com.gistpetition.api.verification.domain;

import lombok.Getter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@MappedSuperclass
public class VerificationInfo {
    public static final int CONFIRM_CODE_EXPIRE_MINUTE = 15;
    public static final int APPLY_EXPIRE_MINUTE = 60;

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
        return time.isAfter(createdAt) && time.isBefore(createdAt.plusMinutes(CONFIRM_CODE_EXPIRE_MINUTE));
    }

    public boolean isConfirmed() {
        return Objects.nonNull(confirmedAt);
    }

    public boolean isValidToApply(LocalDateTime time) {
        return time.isAfter(confirmedAt) && time.isBefore(confirmedAt.plusMinutes(APPLY_EXPIRE_MINUTE));
    }

    public void confirm() {
        confirmedAt = LocalDateTime.now();
    }
}

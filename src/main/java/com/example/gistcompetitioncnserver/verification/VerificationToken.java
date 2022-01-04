package com.example.gistcompetitioncnserver.verification;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class VerificationToken {
    private static final int EXPIRE_MINUTE = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Long userId;
    private LocalDateTime expiryTime;

    protected VerificationToken() {
    }

    public VerificationToken(String token, Long userId) {
        this(null, token, userId, LocalDateTime.now().plusMinutes(EXPIRE_MINUTE));
    }

    public VerificationToken(String token, Long userId, LocalDateTime expiryTime) {
        this(null, token, userId, expiryTime);
    }

    public VerificationToken(Long id, String token, Long userId, LocalDateTime expiryTime) {
        this.id = id;
        this.token = token;
        this.userId = userId;
        this.expiryTime = expiryTime;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public boolean isValidAt(LocalDateTime time) {
        return expiryTime.isAfter(time);
    }
}

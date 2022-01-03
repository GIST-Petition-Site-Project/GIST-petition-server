package com.example.gistcompetitioncnserver.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Long userId;
    private LocalDateTime expiryTime;

    protected VerificationToken() {
    }

    public VerificationToken(String token, User user, int expiryTimeInMinutes) {
        this(null, token, user.getId(), LocalDateTime.now().plusMinutes(expiryTimeInMinutes));
    }

    public VerificationToken(String token, User user, LocalDateTime expiryTime) {
        this(null, token, user.getId(), expiryTime);
    }

    public VerificationToken(Long id, String token, Long userId, LocalDateTime expiryTime) {
        this.id = id;
        this.token = token;
        this.userId = userId;
        this.expiryTime = expiryTime;
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

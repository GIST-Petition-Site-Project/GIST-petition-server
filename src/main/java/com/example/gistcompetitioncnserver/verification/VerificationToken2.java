package com.example.gistcompetitioncnserver.verification;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class VerificationToken2 {
    private static final int EXPIRE_MINUTE = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String token;
    private Boolean confirmed;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;

    public VerificationToken2() {
    }

    public VerificationToken2(String email, String token) {
        this(null, email, token, false, LocalDateTime.now(), null);
    }

    private VerificationToken2(Long id, String email, String token, Boolean confirmed, LocalDateTime createdAt, LocalDateTime confirmedAt) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.confirmed = confirmed;
        this.createdAt = createdAt;
        this.confirmedAt = confirmedAt;
    }
}

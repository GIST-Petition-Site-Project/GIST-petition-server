package com.example.gistcompetitioncnserver.user;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    private LocalDateTime expiryTime;

    protected VerificationToken() {
    }

    public VerificationToken(String token, User user, int expiryTimeInMinutes) {
        this(null, token, user, LocalDateTime.now().plusMinutes(expiryTimeInMinutes));
    }

    public VerificationToken(Long id, String token, User user, LocalDateTime expiryTime) {
        this.id = id;
        this.token = token;
        this.user = user;
        this.expiryTime = expiryTime;
    }

    public User getUser() {
        return user;
    }

    public boolean isValidAt(LocalDateTime time) {
        return expiryTime.isAfter(time);
    }
}

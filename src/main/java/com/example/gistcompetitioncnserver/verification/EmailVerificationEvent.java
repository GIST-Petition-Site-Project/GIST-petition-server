package com.example.gistcompetitioncnserver.verification;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
public class EmailVerificationEvent extends ApplicationEvent {
    private final String email;
    private final String token;

    public EmailVerificationEvent(String email, String token) {
        super(email);
        this.email = email;
        this.token = token;
    }

}

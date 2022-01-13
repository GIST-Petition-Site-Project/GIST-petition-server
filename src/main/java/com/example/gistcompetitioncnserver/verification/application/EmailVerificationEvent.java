package com.example.gistcompetitioncnserver.verification.application;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmailVerificationEvent extends ApplicationEvent {
    private final String username;
    private final String verificationCode;

    public EmailVerificationEvent(String username, String verificationCode) {
        super(username);
        this.username = username;
        this.verificationCode = verificationCode;
    }

}

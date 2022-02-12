package com.gistpetition.api.verification.application;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmailVerificationEvent extends ApplicationEvent {
    private final String username;
    private final String verificationCode;
    private final VerificationType verificationType;

    public EmailVerificationEvent(String username, String verificationCode, VerificationType verificationType) {
        super(username);
        this.username = username;
        this.verificationCode = verificationCode;
        this.verificationType = verificationType;
    }

}

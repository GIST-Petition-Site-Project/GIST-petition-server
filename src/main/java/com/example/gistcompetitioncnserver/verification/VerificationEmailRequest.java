package com.example.gistcompetitioncnserver.verification;

import javax.validation.constraints.Email;

public class VerificationEmailRequest {
    @Email
    private String username;

    public VerificationEmailRequest() {
    }

    public VerificationEmailRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}

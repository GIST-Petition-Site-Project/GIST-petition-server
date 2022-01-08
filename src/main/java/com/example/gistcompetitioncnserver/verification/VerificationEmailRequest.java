package com.example.gistcompetitioncnserver.verification;

public class VerificationEmailRequest {
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

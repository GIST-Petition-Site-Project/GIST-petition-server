package com.example.gistcompetitioncnserver.verification;

public class VerificationEmailRequest {
    private String email;

    public VerificationEmailRequest() {
    }

    public VerificationEmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}

package com.gistpetition.api.verification.dto;

public class UsernameConfirmationRequest {
    private String username;
    private String verificationCode;

    public UsernameConfirmationRequest() {
    }

    public UsernameConfirmationRequest(String username, String verificationCode) {
        this.username = username;
        this.verificationCode = verificationCode;
    }

    public String getUsername() {
        return username;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}

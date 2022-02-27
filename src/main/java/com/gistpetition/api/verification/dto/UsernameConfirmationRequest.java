package com.gistpetition.api.verification.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UsernameConfirmationRequest {
    @Email
    @NotBlank
    private String username;
    @NotBlank
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

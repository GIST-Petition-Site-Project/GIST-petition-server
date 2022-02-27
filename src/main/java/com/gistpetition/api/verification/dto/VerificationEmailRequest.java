package com.gistpetition.api.verification.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class VerificationEmailRequest {
    @Email
    @NotBlank
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

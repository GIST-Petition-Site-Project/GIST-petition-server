package com.gistpetition.api.user.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UpdatePasswordByVerificationRequest {
    @Email
    private String username;
    @NotBlank
    private String password;
    @NotNull
    private String verificationCode;

    public UpdatePasswordByVerificationRequest() {
    }

    public UpdatePasswordByVerificationRequest(String username, String password, String verificationCode) {
        this.username = username;
        this.password = password;
        this.verificationCode = verificationCode;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}

package com.gistpetition.api.user.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class SignInRequest {
    @Email
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    protected SignInRequest() {
    }

    public SignInRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

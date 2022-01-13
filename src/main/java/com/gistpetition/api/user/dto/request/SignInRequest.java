package com.gistpetition.api.user.dto.request;

import javax.validation.constraints.NotNull;

public class SignInRequest {
    @NotNull
    private String username;
    @NotNull
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

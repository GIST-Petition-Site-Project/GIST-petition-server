package com.gistpetition.api.user.dto.request;

import javax.validation.constraints.NotBlank;

public class DeleteUserRequest {
    @NotBlank
    private String password;

    public DeleteUserRequest() {
    }

    public DeleteUserRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}

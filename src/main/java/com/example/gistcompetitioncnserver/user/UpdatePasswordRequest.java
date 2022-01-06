package com.example.gistcompetitioncnserver.user;

import javax.validation.constraints.NotBlank;

public class UpdatePasswordRequest {
    @NotBlank
    private String originPassword;
    @NotBlank
    private String newPassword;

    public UpdatePasswordRequest() {
    }

    public UpdatePasswordRequest(String originPassword, String newPassword) {
        this.originPassword = originPassword;
        this.newPassword = newPassword;
    }

    public String getOriginPassword() {
        return originPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}

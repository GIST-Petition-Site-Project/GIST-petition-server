package com.example.gistcompetitioncnserver.user;

import javax.validation.constraints.NotNull;

public class UpdateUserRoleRequest {
    @NotNull
    private String userRole;

    public UpdateUserRoleRequest() {
    }

    public UpdateUserRoleRequest(String userRole) {
        this.userRole = userRole;
    }

    public String getUserRole() {
        return userRole;
    }
}

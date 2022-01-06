package com.example.gistcompetitioncnserver.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class SessionUser implements Serializable {
    private final Long id;
    private final UserRole userRole;
    private final Boolean enabled;

    public SessionUser(User user) {
        this.id = user.getId();
        this.userRole = user.getUserRole();
        this.enabled = user.isEnabled();
    }

    public boolean isAdmin() {
        return userRole == UserRole.ADMIN;
    }
}

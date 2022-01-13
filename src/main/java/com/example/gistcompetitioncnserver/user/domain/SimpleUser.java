package com.example.gistcompetitioncnserver.user.domain;

import java.io.Serializable;

public class SimpleUser implements Serializable {
    private final Long id;
    private final UserRole userRole;

    public SimpleUser(User user) {
        this.id = user.getId();
        this.userRole = user.getUserRole();
    }

    public boolean isAdmin() {
        return userRole == UserRole.ADMIN;
    }

    public boolean hasManagerAuthority() {
        return userRole == UserRole.MANAGER || userRole == UserRole.ADMIN;
    }

    public Long getId() {
        return id;
    }

    public UserRole getUserRole() {
        return userRole;
    }
}

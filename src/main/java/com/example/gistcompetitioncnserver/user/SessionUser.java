package com.example.gistcompetitioncnserver.user;

import java.io.Serializable;

public class SessionUser implements LoginUser, Serializable {
    private final Long id;
    private final UserRole userRole;

    public SessionUser(User user) {
        this.id = user.getId();
        this.userRole = user.getUserRole();
    }

    public boolean isAdmin() {
        return userRole == UserRole.ADMIN;
    }

    public boolean hasManagerAuthority() {
        return userRole == UserRole.MANAGER || userRole == UserRole.ADMIN;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public UserRole getUserRole() {
        return userRole;
    }
}

package com.example.gistcompetitioncnserver.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class SessionUser implements Serializable {
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
}

package com.example.gistcompetitioncnserver.user;

public interface LoginUser {
    Long getId();

    boolean isAdmin();

    boolean hasManagerAuthority();

    UserRole getUserRole();
}

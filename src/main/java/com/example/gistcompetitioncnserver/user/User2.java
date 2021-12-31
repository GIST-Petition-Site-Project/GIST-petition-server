package com.example.gistcompetitioncnserver.user;

import javax.persistence.*;

@Entity
public class User2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    protected User2() {
    }

    public User2(String username, String password, UserRole userRole) {
        this(null, username, password, userRole);
    }

    private User2(Long id, String username, String password, UserRole userRole) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
    }

    public boolean isAdmin() {
        return this.userRole == UserRole.ADMIN;
    }

    public Long getId() {
        return id;
    }
}

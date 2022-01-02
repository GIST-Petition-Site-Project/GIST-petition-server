package com.example.gistcompetitioncnserver.user;

import javax.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private Boolean enabled;

    protected User() {
    }

    public User(String username, String password, UserRole userRole) {
        this(null, username, password, userRole, false);
    }

    public User(Long id, String username, String password, UserRole userRole, Boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.enabled = enabled;
    }

    public boolean isAdmin() {
        return this.userRole == UserRole.ADMIN;
    }

    public boolean isManager() {
        return this.userRole == UserRole.MANAGER;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}

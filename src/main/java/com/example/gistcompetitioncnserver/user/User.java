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

    public User(String username, String password, UserRole userRole, Boolean enabled) {
        this(null, username, password, userRole, enabled);
    }

    public User(Long id, String username, String password, UserRole userRole, Boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.enabled = enabled;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public void setEnabled() {
        this.enabled = true;
    }
}

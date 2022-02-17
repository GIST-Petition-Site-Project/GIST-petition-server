package com.gistpetition.api.user.domain;

import com.gistpetition.api.common.persistence.BaseEntity;

import javax.persistence.*;

@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    protected User() {
    }

    public User(String username, String password, UserRole userRole) {
        this(null, username, password, userRole);
    }

    public User(String username, String password, UserRole userRole, Boolean enabled) {
        this(null, username, password, userRole);
    }

    public User(Long id, String username, String password, UserRole userRole) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

}

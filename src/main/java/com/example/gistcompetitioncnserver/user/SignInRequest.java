package com.example.gistcompetitioncnserver.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SignInRequest {
    @NotNull
    private final String username;
    @NotNull
    private final String password;
}

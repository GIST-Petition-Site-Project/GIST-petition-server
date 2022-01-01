package com.example.gistcompetitioncnserver.user;

import lombok.Data;

@Data
public class SignUpRequest {
    private final String username;
    private final String password;
}

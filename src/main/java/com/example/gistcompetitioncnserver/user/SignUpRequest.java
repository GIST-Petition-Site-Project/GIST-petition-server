package com.example.gistcompetitioncnserver.user;

import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String password;
}

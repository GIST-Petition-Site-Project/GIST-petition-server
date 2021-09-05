package com.example.gistcompetitioncnserver.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RegistrationRequest {
    private final String username;
    private final String email;
    private final String userId;
    private final String userPassword;
}

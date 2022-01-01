package com.example.gistcompetitioncnserver.registration;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class RegistrationRequest {
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String username;
}

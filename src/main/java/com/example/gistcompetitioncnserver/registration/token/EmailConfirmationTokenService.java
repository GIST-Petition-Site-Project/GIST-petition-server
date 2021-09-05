package com.example.gistcompetitioncnserver.registration.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailConfirmationTokenService {

    @Autowired
    private EmailConfirmationTokenRepository emailConfirmationTokenRepository;

    public void saveEmailConfirmToken(EmailConfirmationToken token){
        emailConfirmationTokenRepository.save(token);
    }

    public Optional<EmailConfirmationToken> getToken(String token) {
        return emailConfirmationTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return emailConfirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }

}

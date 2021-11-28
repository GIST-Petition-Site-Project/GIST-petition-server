package com.example.gistcompetitioncnserver.registration.emailsender;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("!prod")
@Service
public class FakeEmailSender implements EmailSender {
    @Override
    public void send(String to, String email) {
        System.out.println("Send to: " + to + " email: " + email);
    }
}

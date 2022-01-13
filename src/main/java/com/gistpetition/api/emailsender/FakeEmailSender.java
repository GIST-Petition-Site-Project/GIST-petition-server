package com.gistpetition.api.emailsender;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("!dev && !prod")
@Service
public class FakeEmailSender implements EmailSender {
    @Override
    public void send(String to, String subject, String content) {
        System.out.println("Send to: " + to + "subject: " + subject + " email: " + content);
    }
}
package com.gistpetition.api.utils.emailsender;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("!dev && !prod")
@Service
public class FakeEmailSender implements EmailSender {
    @Override
    public void send(String to, String subject, String content) {
        System.out.println("Send to: " + to + "subject: " + subject + " email: " + content);
    }

    @Override
    public void send(List<String> to, String subject, String content) {
        System.out.println("Send to: " + to + "subject: " + subject + " email: " + content);
    }
}

package com.example.gistcompetitioncnserver.emailsender;

public interface EmailSender {
    void send(String to, String subject, String content);
}

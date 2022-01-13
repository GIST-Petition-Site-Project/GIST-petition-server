package com.gistpetition.api.emailsender;

public interface EmailSender {
    void send(String to, String subject, String content);
}

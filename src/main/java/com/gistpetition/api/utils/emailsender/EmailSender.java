package com.gistpetition.api.utils.emailsender;

public interface EmailSender {
    void send(String to, String subject, String content);
}

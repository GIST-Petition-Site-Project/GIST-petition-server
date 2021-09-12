package com.example.gistcompetitioncnserver.registration.emailsender;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender{

    private final static Logger LOGGER = LoggerFactory
            .getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper =
                        new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setText(email, true);
                helper.setTo(to); // email that we will send
                helper.setSubject("Confirm your email");
                helper.setFrom("choieungi@gm.gist.ac.kr", "GIST"); // email sender that show client
                mailSender.send(mimeMessage);

        }catch (MessagingException | UnsupportedEncodingException e) {
            LOGGER.error("이메일을 보내는데 실패했습니다.", e);
            throw new IllegalStateException("이메일을 보내는데 실패했습니다.");
        }

    }
}

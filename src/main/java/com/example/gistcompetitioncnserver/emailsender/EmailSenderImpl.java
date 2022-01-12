package com.example.gistcompetitioncnserver.emailsender;

import com.example.gistcompetitioncnserver.exception.WrappedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Profile({"prod","dev"})
@Service
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailSenderImpl.class);

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setTo(to);
            helper.setFrom(new InternetAddress("choieungi@gm.gist.ac.kr", "GIST"));
            mailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException e) {
            LOGGER.error("이메일을 보내는데 실패했습니다.", e);
            throw new WrappedException("이메일을 보내는데 실패했습니다.", e);
        }
    }
}

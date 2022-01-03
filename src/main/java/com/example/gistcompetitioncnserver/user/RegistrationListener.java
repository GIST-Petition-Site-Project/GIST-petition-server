package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.emailsender.EmailSender;
import com.example.gistcompetitioncnserver.exception.CustomException;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final VerificationService verificationService;
    private final EmailSender mailSender;
    private final SpringTemplateEngine springTemplateEngine;
    private final UserRepository userRepository;

    public RegistrationListener(VerificationService verificationService, EmailSender mailSender, SpringTemplateEngine springTemplateEngine, UserRepository userRepository) {
        this.verificationService = verificationService;
        this.mailSender = mailSender;
        this.springTemplateEngine = springTemplateEngine;
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new CustomException("존재하지 않는 사용자입니다."));
        String token = UUID.randomUUID().toString();
        verificationService.createToken(user.getId(), token);
        String recipientAddress = user.getUsername();
        String subject = "Registration Confirmation";
        String confirmationUrl = event.getAppUrl() + "/confirm?token=" + token;
        Context context = new Context();
        context.setVariable("confirmUrl", confirmationUrl);
        String body = springTemplateEngine.process("email-verification.html", context);

        mailSender.send(recipientAddress, subject, body);
    }
}

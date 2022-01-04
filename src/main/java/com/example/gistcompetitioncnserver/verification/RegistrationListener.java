package com.example.gistcompetitioncnserver.verification;

import com.example.gistcompetitioncnserver.emailsender.EmailSender;
import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.user.OnRegistrationCompleteEvent;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final VerificationService verificationService;
    private final UserRepository userRepository;
    private final EmailSender mailSender;
    private final SpringTemplateEngine springTemplateEngine;

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

        String token = verificationService.createToken(user);

        String mailTo = user.getUsername();
        String subject = "[지스트 청원] 회원 가입 인증 메일";
        String body = generateMailBody(event, token);

        mailSender.send(mailTo, subject, body);
    }

    private String generateMailBody(OnRegistrationCompleteEvent event, String token) {
        Context context = new Context();
        context.setVariable("confirmUrl", event.getAppUrl() + "/confirm?token=" + token);
        return springTemplateEngine.process("email-verification.html", context);
    }
}

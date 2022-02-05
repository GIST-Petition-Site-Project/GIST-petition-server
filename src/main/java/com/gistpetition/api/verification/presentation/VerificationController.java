package com.gistpetition.api.verification.presentation;

import com.gistpetition.api.verification.application.EmailVerificationEvent;
import com.gistpetition.api.verification.application.password.FindPasswordVerificationService;
import com.gistpetition.api.verification.application.signup.SignUpVerificationService;
import com.gistpetition.api.verification.application.VerficationType;
import com.gistpetition.api.verification.dto.UsernameConfirmationRequest;
import com.gistpetition.api.verification.dto.VerificationEmailRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class VerificationController {

    private final SignUpVerificationService signUpVerificationService;
    private final FindPasswordVerificationService findPasswordVerificationService;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/sign-up/verifications")
    public ResponseEntity<Void> createSignUpVerificationCode(@RequestBody VerificationEmailRequest request) {
        String verificationCode = signUpVerificationService.createVerificationInfo(request);
        publisher.publishEvent(new EmailVerificationEvent(request.getUsername(), verificationCode, VerficationType.SignUp));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sign-up/confirm")
    public ResponseEntity<Void> confirmSignUpVerificationCode(@RequestBody UsernameConfirmationRequest request) {
        signUpVerificationService.confirmUsername(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/find-password/verifications")
    public ResponseEntity<Void> createFindPasswordVerificationCode(@RequestBody VerificationEmailRequest request) {
        String verificationCode = findPasswordVerificationService.createPasswordVerificationInfo(request);
        publisher.publishEvent(new EmailVerificationEvent(request.getUsername(), verificationCode, VerficationType.NewPassword));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/find-password/confirm")
    public ResponseEntity<Void> confirmFindPassowrdVerificationCode(@RequestBody UsernameConfirmationRequest request) {
        findPasswordVerificationService.confirmUsername(request);
        return ResponseEntity.noContent().build();
    }


}

package com.gistpetition.api.verification.presentation;

import com.gistpetition.api.verification.application.EmailVerificationEvent;
import com.gistpetition.api.verification.application.VerficationType;
import com.gistpetition.api.verification.application.VerificationService;
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

    private final VerificationService verificationService;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/sign_up/verifications")
    public ResponseEntity<Void> createSignUpVerificationCode(@RequestBody VerificationEmailRequest request) {
        String verificationCode = verificationService.createVerificationInfo(request);
        publisher.publishEvent(new EmailVerificationEvent(request.getUsername(), verificationCode, VerficationType.SignUp));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sign_up/confirm")
    public ResponseEntity<Void> confirmSignUpVerificationCode(@RequestBody UsernameConfirmationRequest request) {
        verificationService.confirmUsername(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/find_password/verifications")
    public ResponseEntity<Void> createFindPasswordVerificationCode(@RequestBody VerificationEmailRequest request) {
        String verificationCode = verificationService.createVerificationInfo(request); //todo change service
        publisher.publishEvent(new EmailVerificationEvent(request.getUsername(), verificationCode, VerficationType.NewPassword));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/find_password/confirm")
    public ResponseEntity<Void> confirmFindPassowrdVerificationCode(@RequestBody UsernameConfirmationRequest request) {
        verificationService.confirmUsername(request); //todo change service
        return ResponseEntity.noContent().build();
    }


}

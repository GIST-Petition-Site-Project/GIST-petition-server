package com.example.gistcompetitioncnserver.verification;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/username/verifications")
    public ResponseEntity<Void> createVerificationCode(@RequestBody VerificationEmailRequest request){
        String verificationCode = verificationService.createVerificationInfo(request);
        publisher.publishEvent(new EmailVerificationEvent(request.getUsername(), verificationCode));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/username/confirm")
    public ResponseEntity<Void> confirmVerificationCode(@RequestBody UsernameConfirmationRequest request){
        verificationService.confirmUsername(request);
        return ResponseEntity.noContent().build();
    }
}

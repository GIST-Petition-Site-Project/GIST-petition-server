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

    @GetMapping("/users/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        verificationService.confirm(token);
        return ResponseEntity.ok().body("인증되었습니다.");
    }

    @PostMapping("/verification-email")
    public ResponseEntity<Void> createVerificationToken(@RequestBody VerificationEmailRequest request){
        String token = verificationService.createVerificationInfo(request);
        publisher.publishEvent(new EmailVerificationEvent(request.getUsername(), token));
        return ResponseEntity.noContent().build();
    }

}

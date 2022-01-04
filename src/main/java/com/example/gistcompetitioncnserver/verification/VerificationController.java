package com.example.gistcompetitioncnserver.verification;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping("/users/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        verificationService.confirm(token);
        return ResponseEntity.ok().body("인증되었습니다.");
    }
}

package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.emailsender.EmailSender;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final VerificationService verificationService;

    @PostMapping("/users")
    public ResponseEntity<Void> register(@Validated @RequestBody SignUpRequest request) {
        Long userId = userService.signUp(request);
        return ResponseEntity.created(URI.create("/users/" + userId)).build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> register(@RequestParam String token) {
        verificationService.confirm(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/users/{userId}")
    public User retrieveUser(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}

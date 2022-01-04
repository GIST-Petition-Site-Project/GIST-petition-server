package com.example.gistcompetitioncnserver.user;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/users")
    public ResponseEntity<Void> register(@Validated @RequestBody SignUpRequest signUpRequest, HttpServletRequest request) {
        Long userId = userService.signUp(signUpRequest);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userId, request.getRequestURL().toString()));
        return ResponseEntity.created(URI.create("/users/" + userId)).build();
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

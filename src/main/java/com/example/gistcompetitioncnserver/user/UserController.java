package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.exception.CustomException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/users")
    public ResponseEntity<Void> register(@Validated @RequestBody SignUpRequest request) {
        return ResponseEntity.created(URI.create("/users/" + userService.signUp(request))).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Validated @RequestBody SignInRequest request) {
        try {
            userService.signIn(request);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
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

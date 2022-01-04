package com.example.gistcompetitioncnserver.user;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final HttpSession httpSession;
    @PostMapping("/users")
    public ResponseEntity<Void> register(@Validated @RequestBody SignUpRequest request) {
        return ResponseEntity.created(URI.create("/users/" + userService.signUp(request))).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Validated @RequestBody SignInRequest request) {
        userService.signIn(request);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(){
        httpSession.removeAttribute("user");
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

package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.exception.user.NotConfirmedEmailException;
import com.example.gistcompetitioncnserver.exception.user.UnAuthorizedUserException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final HttpSession httpSession;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/users")
    public ResponseEntity<Void> register(@Validated @RequestBody SignUpRequest signUpRequest, HttpServletRequest request) {
        Long userId = userService.signUp(signUpRequest);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userId, request.getRequestURL().toString()));
        return ResponseEntity.created(URI.create("/users/" + userId)).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Validated @RequestBody SignInRequest request) {
        userService.signIn(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        httpSession.invalidate();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> retrieveAllUsers() {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new NotConfirmedEmailException();
        }
        if (!sessionUser.isAdmin()) {
            throw new UnAuthorizedUserException();
        }
        return ResponseEntity.ok().body(userService.findAllUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> retrieveUser(@PathVariable Long userId) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new NotConfirmedEmailException();
        }
        if (!sessionUser.isAdmin()) {
            throw new UnAuthorizedUserException();
        }
        return ResponseEntity.ok().body(userService.findUserById(userId));
    }

    @GetMapping("/users/me")
    public ResponseEntity<User> retrieveUserOfMine() {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new NotConfirmedEmailException();
        }
        return ResponseEntity.ok().body(userService.findUserById(sessionUser.getId()));
    }

    @PutMapping("/users/{userId}/userRole")
    public ResponseEntity<Void> updateUserRole(@PathVariable Long userId,
                                               @Validated @RequestBody UpdateUserRoleRequest userRoleRequest) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new NotConfirmedEmailException();
        }
        if (!sessionUser.isAdmin()) {
            throw new UnAuthorizedUserException();
        }
        userService.updateUserRole(userId, userRoleRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/me/password")
    public ResponseEntity<Void> updatePasswordOfMine(@Validated @RequestBody UpdatePasswordRequest passwordRequest) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new NotConfirmedEmailException();
        }
        userService.updatePassword(sessionUser.getId(), passwordRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new NotConfirmedEmailException();
        }
        if (!sessionUser.isAdmin()) {
            throw new UnAuthorizedUserException();
        }
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/me")
    public ResponseEntity<Void> deleteUserOfMine(@Validated @RequestBody DeleteUserRequest deleteUserRequest) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new NotConfirmedEmailException();
        }
        userService.deleteUserOfMine(sessionUser.getId(), deleteUserRequest);
        return ResponseEntity.noContent().build();
    }
}

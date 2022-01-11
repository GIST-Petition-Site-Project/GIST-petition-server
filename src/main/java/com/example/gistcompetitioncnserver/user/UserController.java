package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.config.annotation.AdminPermissionRequired;
import com.example.gistcompetitioncnserver.config.annotation.LoginRequired;
import com.example.gistcompetitioncnserver.config.annotation.LoginUser;
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
    private final LoginService loginService;

    @PostMapping("/users")
    public ResponseEntity<Void> register(@Validated @RequestBody SignUpRequest signUpRequest) {
        Long userId = userService.signUp(signUpRequest);
        return ResponseEntity.created(URI.create("/users/" + userId)).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Validated @RequestBody SignInRequest request) {
        loginService.login(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        loginService.logout();
        return ResponseEntity.noContent().build();
    }

    @AdminPermissionRequired
    @GetMapping("/users")
    public ResponseEntity<List<User>> retrieveAllUsers() {
        return ResponseEntity.ok().body(userService.findAllUsers());
    }

    @AdminPermissionRequired
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> retrieveUser(@PathVariable Long userId) {
        return ResponseEntity.ok().body(userService.findUserById(userId));
    }

    @LoginRequired
    @GetMapping("/users/me")
    public ResponseEntity<User> retrieveUserOfMine(@LoginUser SessionUser sessionUser) {
        return ResponseEntity.ok().body(userService.findUserById(sessionUser.getId()));
    }

    @AdminPermissionRequired
    @PutMapping("/users/{userId}/userRole")
    public ResponseEntity<Void> updateUserRole(@PathVariable Long userId,
                                               @Validated @RequestBody UpdateUserRoleRequest userRoleRequest) {
        userService.updateUserRole(userId, userRoleRequest);
        return ResponseEntity.noContent().build();
    }

    @LoginRequired
    @PutMapping("/users/me/password")
    public ResponseEntity<Void> updatePasswordOfMine(@Validated @RequestBody UpdatePasswordRequest passwordRequest,
                                                     @LoginUser SessionUser sessionUser) {
        userService.updatePassword(sessionUser.getId(), passwordRequest);
        return ResponseEntity.noContent().build();
    }

    @AdminPermissionRequired
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @LoginRequired
    @DeleteMapping("/users/me")
    public ResponseEntity<Void> deleteUserOfMine(@Validated @RequestBody DeleteUserRequest deleteUserRequest,
                                                 @LoginUser SessionUser sessionUser) {
        userService.deleteUserOfMine(sessionUser.getId(), deleteUserRequest);
        return ResponseEntity.noContent().build();
    }
}

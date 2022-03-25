package com.gistpetition.api.user.presentation;

import com.gistpetition.api.config.annotation.AdminPermissionRequired;
import com.gistpetition.api.config.annotation.LoginRequired;
import com.gistpetition.api.config.annotation.LoginUser;
import com.gistpetition.api.user.application.LoginService;
import com.gistpetition.api.user.application.UserService;
import com.gistpetition.api.user.domain.SimpleUser;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.user.dto.request.*;
import com.gistpetition.api.user.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final LoginService loginService;

    @PostMapping("/users")
    public ResponseEntity<Void> register(@Validated @RequestBody SignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<Page<UserResponse>> retrieveUsers(@RequestParam(required = false) String userRole, Pageable pageable) {
        if (Objects.isNull(userRole)) {
            return ResponseEntity.ok().body(userService.retrieveUsers(pageable));
        }
        return ResponseEntity.ok().body(userService.retrieveUsersOfUserRole(UserRole.ignoringCaseValueOf(userRole), pageable));
    }

    @LoginRequired
    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> retrieveUserOfMine(@LoginUser SimpleUser simpleUser) {
        return ResponseEntity.ok().body(UserResponse.of(userService.findUserById(simpleUser.getId())));
    }

    @AdminPermissionRequired
    @PutMapping("/users/{username}/userRole")
    public ResponseEntity<Void> updateUserRole(@PathVariable String username,
                                               @Validated @RequestBody UpdateUserRoleRequest userRoleRequest) {
        userService.updateUserRole(username, userRoleRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/reset-password")
    public ResponseEntity<Void> updatePasswordByVerification(@Validated @RequestBody UpdatePasswordByVerificationRequest request) {
        userService.updatePasswordByVerificationCode(request);
        return ResponseEntity.noContent().build();
    }

    @LoginRequired
    @PutMapping("/users/me/password")
    public ResponseEntity<Void> updatePasswordOfMine(@Validated @RequestBody UpdatePasswordRequest passwordRequest,
                                                     @LoginUser SimpleUser simpleUser) {
        userService.updatePassword(simpleUser.getId(), passwordRequest);
        return ResponseEntity.noContent().build();
    }

    @AdminPermissionRequired
    @DeleteMapping("/users/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @LoginRequired
    @DeleteMapping("/users/me")
    public ResponseEntity<Void> deleteUserOfMine(@Validated @RequestBody DeleteUserRequest deleteUserRequest,
                                                 @LoginUser SimpleUser simpleUser) {
        userService.deleteUserOfMine(simpleUser.getId(), deleteUserRequest);
        return ResponseEntity.noContent().build();
    }
}

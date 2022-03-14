package com.gistpetition.api.user.application;

import com.gistpetition.api.config.annotation.DataIntegrityHandler;
import com.gistpetition.api.exception.user.DuplicatedUserException;
import com.gistpetition.api.exception.user.InvalidEmailFormException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.exception.user.NotMatchedPasswordException;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.user.dto.request.*;
import com.gistpetition.api.user.dto.response.UserResponse;
import com.gistpetition.api.utils.email.EmailDomain;
import com.gistpetition.api.utils.email.EmailParser;
import com.gistpetition.api.utils.password.Encoder;
import com.gistpetition.api.verification.application.password.FindPasswordValidator;
import com.gistpetition.api.verification.application.signup.SignUpValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final Encoder encoder;
    private final SignUpValidator signUpValidator;
    private final FindPasswordValidator passwordValidator;


    @Transactional
    @DataIntegrityHandler(DuplicatedUserException.class)
    public Long signUp(SignUpRequest request) {
        String username = request.getUsername();
        String verificationCode = request.getVerificationCode();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicatedUserException();
        }
        if (!EmailDomain.has(EmailParser.parseDomainFrom(username))) {
            throw new InvalidEmailFormException();
        }
        signUpValidator.checkIsVerified(username, verificationCode);

        User user = new User(username, encoder.hashPassword(request.getPassword()), UserRole.USER);
        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(NoSuchUserException::new);
    }

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(NoSuchUserException::new);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> retrieveUsers(Pageable pageable) {
        return UserResponse.pageOf(userRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> retrieveUsersOfUserRole(UserRole userRole, Pageable pageable) {
        return UserResponse.pageOf(userRepository.findAllByUserRole(userRole, pageable));
    }

    @Transactional
    public void updateUserRole(String username, UpdateUserRoleRequest userRoleRequest) {
        User user = findUserByUsername(username);
        user.setUserRole(UserRole.ignoringCaseValueOf(userRoleRequest.getUserRole()));
    }

    @Transactional
    public void updatePasswordByVerificationCode(UpdatePasswordByVerificationRequest request) {
        User user = findUserByUsername(request.getUsername());
        passwordValidator.checkIsVerified(user.getUsername(), request.getVerificationCode());
        user.setPassword(encoder.hashPassword(request.getPassword()));
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest passwordRequest) {
        User user = findUserById(userId);
        if (!encoder.isMatch(passwordRequest.getOriginPassword(), user.getPassword())) {
            throw new NotMatchedPasswordException();
        }
        user.setPassword(encoder.hashPassword(passwordRequest.getNewPassword()));
    }

    @Transactional
    public void deleteUser(String username) {
        User user = findUserByUsername(username);
        userRepository.delete(user);
    }

    @Transactional
    public void deleteUserOfMine(Long userId, DeleteUserRequest deleteUserRequest) {
        User user = findUserById(userId);
        if (!encoder.isMatch(deleteUserRequest.getPassword(), user.getPassword())) {
            throw new NotMatchedPasswordException();
        }
        userRepository.deleteById(userId);
    }
}

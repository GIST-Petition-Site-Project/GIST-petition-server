package com.gistpetition.api.user.application;

import com.gistpetition.api.exception.user.DuplicatedUserException;
import com.gistpetition.api.exception.user.InvalidEmailFormException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.exception.user.NotMatchedPasswordException;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.user.dto.request.*;
import com.gistpetition.api.utils.email.EmailDomain;
import com.gistpetition.api.utils.email.EmailParser;
import com.gistpetition.api.utils.password.Encoder;
import com.gistpetition.api.verification.application.password.FindPasswordValidator;
import com.gistpetition.api.verification.application.signup.SignUpValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        try {
            return userRepository.save(user).getId();
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicatedUserException();
        }
    }

    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(NoSuchUserException::new);
    }

    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return userRepository.findByUsername(email)
                .orElseThrow(NoSuchUserException::new);
    }

    @Transactional(readOnly = true)
    public Page<User> retrieveUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public void updateUserRole(Long userId, UpdateUserRoleRequest userRoleRequest) {
        User user = findUserById(userId);
        user.setUserRole(UserRole.ignoringCaseValueOf(userRoleRequest.getUserRole()));
    }

    @Transactional
    public void updatePasswordByVerificationCode(UpdatePasswordByVerificationRequest request) {
        User user = findUserByEmail(request.getUsername());
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
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUserException();
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void deleteUserOfMine(Long userId, DeleteUserRequest deleteUserRequest) {
        User user = findUserById(userId);
        if (!encoder.isMatch(deleteUserRequest.getPassword(), user.getPassword())) {
            throw new NotMatchedPasswordException();
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void deleteUserOfUsername(String username) {
        User user = findUserByEmail(username);
        userRepository.delete(user);
    }
}

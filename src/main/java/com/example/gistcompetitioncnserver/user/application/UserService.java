package com.example.gistcompetitioncnserver.user.application;

import com.example.gistcompetitioncnserver.common.email.EmailDomain;
import com.example.gistcompetitioncnserver.common.email.EmailParser;
import com.example.gistcompetitioncnserver.common.password.Encoder;
import com.example.gistcompetitioncnserver.exception.user.DuplicatedUserException;
import com.example.gistcompetitioncnserver.exception.user.InvalidEmailFormException;
import com.example.gistcompetitioncnserver.exception.user.NoSuchUserException;
import com.example.gistcompetitioncnserver.exception.user.NotMatchedPasswordException;
import com.example.gistcompetitioncnserver.user.domain.User;
import com.example.gistcompetitioncnserver.user.domain.UserRepository;
import com.example.gistcompetitioncnserver.user.domain.UserRole;
import com.example.gistcompetitioncnserver.user.dto.request.DeleteUserRequest;
import com.example.gistcompetitioncnserver.user.dto.request.SignUpRequest;
import com.example.gistcompetitioncnserver.user.dto.request.UpdatePasswordRequest;
import com.example.gistcompetitioncnserver.user.dto.request.UpdateUserRoleRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Encoder encoder;
    private final SignUpValidator signUpValidator;

    public UserService(UserRepository userRepository, Encoder encoder, SignUpValidator signUpValidator) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.signUpValidator = signUpValidator;
    }

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
        return userRepository.save(user).getId();
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
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void updateUserRole(Long userId, UpdateUserRoleRequest userRoleRequest) {
        User user = findUserById(userId);
        user.setUserRole(UserRole.ignoringCaseValueOf(userRoleRequest.getUserRole()));
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
}

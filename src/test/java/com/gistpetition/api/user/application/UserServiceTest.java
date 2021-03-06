package com.gistpetition.api.user.application;

import com.gistpetition.api.IntegrationTest;
import com.gistpetition.api.exception.user.DuplicatedUserException;
import com.gistpetition.api.exception.user.InvalidEmailFormException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.exception.user.NotMatchedPasswordException;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.user.dto.request.*;
import com.gistpetition.api.user.dto.response.UserResponse;
import com.gistpetition.api.utils.password.Encoder;
import com.gistpetition.api.verification.application.password.FindPasswordValidator;
import com.gistpetition.api.verification.application.signup.SignUpValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;

class UserServiceTest extends IntegrationTest {
    private static final String GIST_EMAIL = "tester@gist.ac.kr";
    private static final String PASSWORD = "password!";
    private static final String VERIFICATION_CODE = "AAAAAA";
    private static final SignUpRequest DEFAULT_SIGN_UP_REQUEST = new SignUpRequest(GIST_EMAIL, PASSWORD, VERIFICATION_CODE);

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Encoder encoder;
    @MockBean
    private SignUpValidator signUpValidator;
    @MockBean
    private FindPasswordValidator passwordValidator;

    @BeforeEach
    void setUp() {
        doNothing().when(signUpValidator).checkIsVerified(any(), eq(VERIFICATION_CODE));
        doNothing().when(passwordValidator).checkIsVerified(any(), eq(VERIFICATION_CODE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"email@gist.ac.kr", "email@gm.gist.ac.kr"})
    void signUp(String email) {
        SignUpRequest signUpRequest = new SignUpRequest(email, PASSWORD, VERIFICATION_CODE);
        Long userId = userService.signUp(signUpRequest);

        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        assertThat(userId).isNotNull();
        assertThat(user.getUsername()).isEqualTo(email);
        assertTrue(encoder.isMatch(PASSWORD, user.getPassword()));
        assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void signUpFailedIfAlreadyExisted() {
        userRepository.save(new User(GIST_EMAIL, PASSWORD, UserRole.USER));

        assertThatThrownBy(() -> userService.signUp(DEFAULT_SIGN_UP_REQUEST)).isInstanceOf(DuplicatedUserException.class);
    }

    @Test
    void signUpFailedIfNotValidEmailForm() {
        String notGistEmail = "email@email.com";
        SignUpRequest signUpRequest = new SignUpRequest(notGistEmail, PASSWORD, VERIFICATION_CODE);

        assertThatThrownBy(() -> userService.signUp(signUpRequest)).isInstanceOf(InvalidEmailFormException.class);
    }

    @Test
    void signUpWithConcurrency() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger errorCount = new AtomicInteger(0);
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                try {
                    userService.signUp(DEFAULT_SIGN_UP_REQUEST);
                } catch (DuplicatedUserException e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        assertThat(errorCount.get()).isEqualTo(numberOfThreads - 1);
        assertThat(userRepository.findAllByUsername(DEFAULT_SIGN_UP_REQUEST.getUsername())).hasSize(1);
    }

    @Test
    void retrieveUser() {
        User saved = userRepository.save(new User(GIST_EMAIL, PASSWORD, UserRole.USER));

        User retrievedUser = userService.findUserById(saved.getId());
        assertThat(retrievedUser.getUsername()).isEqualTo(saved.getUsername());
        assertThat(retrievedUser.getPassword()).isEqualTo(saved.getPassword());
        assertThat(retrievedUser.getUserRole()).isEqualTo(saved.getUserRole());
    }

    @Test
    void retrieveUsers() {
        for (int i = 0; i < 10; i++) {
            userRepository.save(new User(i + GIST_EMAIL, PASSWORD, UserRole.USER));
        }

        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<UserResponse> userResponses = userService.retrieveUsers(pageRequest);
        assertThat(userResponses).hasSize(10);
    }

    @Test
    void retrieveUsersByUserRole() {
        int userCount = 3;
        int managerCount = 2;
        for (int i = 0; i < userCount; i++) {
            userRepository.save(new User(i + GIST_EMAIL, PASSWORD, UserRole.USER));
        }
        for (int i = userCount; i < userCount + managerCount; i++) {
            userRepository.save(new User(i + GIST_EMAIL, PASSWORD, UserRole.MANAGER));
        }
        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<UserResponse> users = userService.retrieveUsersOfUserRole(UserRole.USER, pageRequest);
        assertThat(users).hasSize(userCount);
        Page<UserResponse> managers = userService.retrieveUsersOfUserRole(UserRole.MANAGER, pageRequest);
        assertThat(managers).hasSize(managerCount);
        Page<UserResponse> admins = userService.retrieveUsersOfUserRole(UserRole.ADMIN, pageRequest);
        assertThat(admins).hasSize(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"manager", "Manager", "MANAGER"})
    void updateUserRoleToManager(String inputUserRole) {
        Long userId = userService.signUp(DEFAULT_SIGN_UP_REQUEST);

        UpdateUserRoleRequest userRoleRequest = new UpdateUserRoleRequest(inputUserRole);
        userService.updateUserRole(DEFAULT_SIGN_UP_REQUEST.getUsername(), userRoleRequest);

        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        Assertions.assertThat(user.getUserRole()).isEqualTo(UserRole.MANAGER);
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "Admin", "ADMIN"})
    void updateUserRoleToAdmin(String inputUserRole) {
        Long userId = userService.signUp(DEFAULT_SIGN_UP_REQUEST);

        UpdateUserRoleRequest userRoleRequest = new UpdateUserRoleRequest(inputUserRole);
        userService.updateUserRole(DEFAULT_SIGN_UP_REQUEST.getUsername(), userRoleRequest);

        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        Assertions.assertThat(user.getUserRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void updateUserPasswordByVerification() {
        Long userId = userService.signUp(DEFAULT_SIGN_UP_REQUEST);
        String newPassword = "new" + PASSWORD;
        UpdatePasswordByVerificationRequest passwordRequest = new UpdatePasswordByVerificationRequest(GIST_EMAIL, newPassword, VERIFICATION_CODE);
        userService.updatePasswordByVerificationCode(passwordRequest);

        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        assertTrue(encoder.isMatch(passwordRequest.getPassword(), user.getPassword()));
    }

    @Test
    void updateUserPassword() {
        Long userId = userService.signUp(DEFAULT_SIGN_UP_REQUEST);

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(PASSWORD, "newPassword");
        userService.updatePassword(userId, updatePasswordRequest);

        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        assertTrue(encoder.isMatch(updatePasswordRequest.getNewPassword(), user.getPassword()));
    }

    @Test
    void updateFailIfInvalidOriginPassword() {
        Long userId = userService.signUp(DEFAULT_SIGN_UP_REQUEST);

        UpdatePasswordRequest invalidRequest = new UpdatePasswordRequest("InvalidPassword", "newPassword");

        assertThatThrownBy(
                () -> userService.updatePassword(userId, invalidRequest)
        ).isInstanceOf(NotMatchedPasswordException.class);
    }

    @Test
    void deleteUser() {
        Long userId = userService.signUp(DEFAULT_SIGN_UP_REQUEST);

        userService.deleteUser(DEFAULT_SIGN_UP_REQUEST.getUsername());

        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void deleteUserIfNotExisted() {
        assertThatThrownBy(() -> userService.deleteUser("notSaved@gist.ac.kr")).isInstanceOf(NoSuchUserException.class);
    }

    @Test
    void deleteUserOfMine() {
        Long userId = userService.signUp(DEFAULT_SIGN_UP_REQUEST);

        userService.deleteUserOfMine(userId, new DeleteUserRequest(DEFAULT_SIGN_UP_REQUEST.getPassword()));

        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void deleteUserOfMineWithInvalidPassword() {
        Long userId = userService.signUp(DEFAULT_SIGN_UP_REQUEST);

        assertThatThrownBy(
                () -> userService.deleteUserOfMine(userId, new DeleteUserRequest("notPassword"))
        ).isInstanceOf(NotMatchedPasswordException.class);
    }
}

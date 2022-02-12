package com.gistpetition.api.user;

import com.gistpetition.api.exception.user.DuplicatedUserException;
import com.gistpetition.api.exception.user.InvalidEmailFormException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.exception.user.NotMatchedPasswordException;
import com.gistpetition.api.user.application.UserService;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.user.dto.request.*;
import com.gistpetition.api.utils.password.Encoder;
import com.gistpetition.api.verification.application.password.FindPasswordValidator;
import com.gistpetition.api.verification.application.signup.SignUpValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
class UserServiceTest {
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
        Page<User> users = userService.retrieveUsers(pageRequest);
        assertThat(users).hasSize(10);
    }

    @ParameterizedTest
    @ValueSource(strings = {"manager", "Manager", "MANAGER"})
    void updateUserRoleToManager(String inputUserRole) {
        Long userId = userService.signUp(DEFAULT_SIGN_UP_REQUEST);

        UpdateUserRoleRequest userRoleRequest = new UpdateUserRoleRequest(inputUserRole);
        userService.updateUserRole(userId, userRoleRequest);

        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        Assertions.assertThat(user.getUserRole()).isEqualTo(UserRole.MANAGER);
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "Admin", "ADMIN"})
    void updateUserRoleToAdmin(String inputUserRole) {
        Long userId = userService.signUp(DEFAULT_SIGN_UP_REQUEST);

        UpdateUserRoleRequest userRoleRequest = new UpdateUserRoleRequest(inputUserRole);
        userService.updateUserRole(userId, userRoleRequest);

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

        userService.deleteUser(userId);

        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void deleteUserIfNotExisted() {
        Long notExistedId = Long.MAX_VALUE;

        assertThatThrownBy(() -> userService.deleteUser(notExistedId)).isInstanceOf(NoSuchUserException.class);
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

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }
}

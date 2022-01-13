package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.exception.user.DuplicatedUserException;
import com.example.gistcompetitioncnserver.exception.user.InvalidEmailFormException;
import com.example.gistcompetitioncnserver.exception.user.NoSuchUserException;
import com.example.gistcompetitioncnserver.exception.user.NotMatchedPasswordException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.servlet.http.HttpSession;

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
    private Encryptor encoder;
    @Autowired
    private HttpSession httpSession;
    @MockBean
    private SignUpValidator signUpValidator;

    @BeforeEach
    void setUp() {
        doNothing().when(signUpValidator).checkIsVerified(any(), eq(VERIFICATION_CODE));
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
        userService.signUp(DEFAULT_SIGN_UP_REQUEST);

        assertThatThrownBy(() -> userService.signUp(DEFAULT_SIGN_UP_REQUEST)).isInstanceOf(DuplicatedUserException.class);
    }

    @Test
    void signUpFailedIfNotValidEmailForm() {
        String notGistEmail = "email@email.com";
        SignUpRequest signUpRequest = new SignUpRequest(notGistEmail, PASSWORD, VERIFICATION_CODE);

        assertThatThrownBy(() -> userService.signUp(signUpRequest)).isInstanceOf(InvalidEmailFormException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"manager", "Manager", "MANAGER"})
    void updateUserRoleToManager(String inputUserRole) {
        Long userId = userService.signUp(DEFAULT_SIGN_UP_REQUEST);

        UpdateUserRoleRequest userRoleRequest = new UpdateUserRoleRequest(inputUserRole);
        userService.updateUserRole(userId, userRoleRequest);

        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        assertThat(user.getUserRole()).isEqualTo(UserRole.MANAGER);
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "Admin", "ADMIN"})
    void updateUserRoleToAdmin(String inputUserRole) {
        Long userId = userService.signUp(DEFAULT_SIGN_UP_REQUEST);

        UpdateUserRoleRequest userRoleRequest = new UpdateUserRoleRequest(inputUserRole);
        userService.updateUserRole(userId, userRoleRequest);

        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        assertThat(user.getUserRole()).isEqualTo(UserRole.ADMIN);
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

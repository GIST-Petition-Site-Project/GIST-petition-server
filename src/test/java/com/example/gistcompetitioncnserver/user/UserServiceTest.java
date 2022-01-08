package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.verification.VerificationTokenRepository;
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
    private VerificationTokenRepository verificationTokenRepository;
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

        assertThatThrownBy(() -> userService.signUp(DEFAULT_SIGN_UP_REQUEST)).isInstanceOf(CustomException.class);
    }

    @Test
    void signUpFailedIfNotValidEmailForm() {
        String notGistEmail = "email@email.com";
        SignUpRequest signUpRequest = new SignUpRequest(notGistEmail, PASSWORD, VERIFICATION_CODE);

        assertThatThrownBy(() -> userService.signUp(signUpRequest)).isInstanceOf(CustomException.class);
    }

    @Test
    void signIn() {
        User registeredUser = userRepository.save(new User(GIST_EMAIL, encoder.hashPassword(PASSWORD), UserRole.USER));
        SignInRequest signInRequest = new SignInRequest(GIST_EMAIL, PASSWORD);

        userService.signIn(signInRequest);

        assertTrue(httpSession.isNew());
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        assertThat(sessionUser.getId()).isEqualTo(registeredUser.getId());
        assertThat(sessionUser.getUserRole()).isEqualTo(registeredUser.getUserRole());
    }

    @Test
    void signInFailedIfNotValidUsername() {
        userRepository.save(new User(GIST_EMAIL, encoder.hashPassword(PASSWORD), UserRole.USER));
        String fakeUsername = "wrong@gist.ac.kr";
        SignInRequest signInRequest = new SignInRequest(fakeUsername, PASSWORD);

        assertThatThrownBy(
                () -> userService.signIn(signInRequest)
        ).isInstanceOf(CustomException.class);
        assertThat(httpSession.getAttribute("user")).isNull();
    }

    @Test
    void signInFailedIfNotValidPassword() {
        userRepository.save(new User(GIST_EMAIL, encoder.hashPassword(PASSWORD), UserRole.USER));
        String fakePassword = "wrongpassword";
        SignInRequest signInRequest = new SignInRequest(GIST_EMAIL, fakePassword);

        assertThatThrownBy(
                () -> userService.signIn(signInRequest)
        ).isInstanceOf(CustomException.class);
        assertThat(httpSession.getAttribute("user")).isNull();
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
        ).isInstanceOf(CustomException.class);
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

        assertThatThrownBy(() -> userService.deleteUser(notExistedId)).isInstanceOf(CustomException.class);
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
        ).isInstanceOf(CustomException.class);
    }

    @AfterEach
    void tearDown() {
        verificationTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
}

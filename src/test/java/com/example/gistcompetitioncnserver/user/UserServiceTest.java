package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.exception.user.DuplicatedUserException;
import com.example.gistcompetitioncnserver.exception.user.InvalidEmailFormException;
import com.example.gistcompetitioncnserver.exception.user.NoSuchUserException;
import com.example.gistcompetitioncnserver.exception.user.NotMatchedPasswordException;
import com.example.gistcompetitioncnserver.verification.VerificationTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserServiceTest {
    private static final String GIST_EMAIL = "tester@gist.ac.kr";
    private static final String PASSWORD = "password!";

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private BcryptEncoder encoder;
    @Autowired
    private HttpSession httpSession;

    @ParameterizedTest
    @ValueSource(strings = {"email@gist.ac.kr", "email@gm.gist.ac.kr"})
    void signUp(String email) {
        SignUpRequest signUpRequest = new SignUpRequest(email, PASSWORD);

        Long userId = userService.signUp(signUpRequest);

        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        assertThat(userId).isNotNull();
        assertThat(user.getUsername()).isEqualTo(email);
        assertTrue(encoder.isMatch(PASSWORD, user.getPassword()));
        assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void signUpFailedIfAlreadyExisted() {
        SignUpRequest signUpRequest = new SignUpRequest(GIST_EMAIL, PASSWORD);
        userService.signUp(signUpRequest);

        assertThatThrownBy(() -> userService.signUp(signUpRequest)).isInstanceOf(DuplicatedUserException.class);
    }

    @Test
    void signUpFailedIfNotValidEmailForm() {
        String notGistEmail = "email@email.com";
        SignUpRequest signUpRequest = new SignUpRequest(notGistEmail, PASSWORD);

        assertThatThrownBy(() -> userService.signUp(signUpRequest)).isInstanceOf(InvalidEmailFormException.class);
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
        User registeredUser = userRepository.save(new User(GIST_EMAIL, encoder.hashPassword(PASSWORD), UserRole.USER));
        String fakeUsername = "wrong@gist.ac.kr";
        SignInRequest signInRequest = new SignInRequest(fakeUsername, PASSWORD);

        assertThatThrownBy(
                () -> userService.signIn(signInRequest)
        ).isInstanceOf(NoSuchUserException.class);
        assertThat(httpSession.getAttribute("user")).isNull();
    }

    @Test
    void signInFailedIfNotValidPassword() {
        User registeredUser = userRepository.save(new User(GIST_EMAIL, encoder.hashPassword(PASSWORD), UserRole.USER));
        String fakePassword = "wrongpassword";
        SignInRequest signInRequest = new SignInRequest(GIST_EMAIL, fakePassword);

        assertThatThrownBy(
                () -> userService.signIn(signInRequest)
        ).isInstanceOf(NotMatchedPasswordException.class);
        assertThat(httpSession.getAttribute("user")).isNull();
    }

    @Test
    void deleteUser() {
        SignUpRequest signUpRequest = new SignUpRequest(GIST_EMAIL, PASSWORD);
        Long userId = userService.signUp(signUpRequest);

        userService.deleteUser(userId);

        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void deleteUserIfNotExisted() {
        Long notExistedId = Long.MAX_VALUE;

        assertThatThrownBy(() -> userService.deleteUser(notExistedId)).isInstanceOf(NoSuchUserException.class);
    }

    @AfterEach
    void tearDown() {
        verificationTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
}

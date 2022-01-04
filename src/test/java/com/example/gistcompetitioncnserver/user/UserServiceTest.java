package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.verification.VerificationTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

        assertThatThrownBy(() -> userService.signUp(signUpRequest)).isInstanceOf(CustomException.class);
    }

    @Test
    void signUpFailedIfNotValidEmailForm() {
        String notGistEmail = "email@email.com";
        SignUpRequest signUpRequest = new SignUpRequest(notGistEmail, PASSWORD);

        assertThatThrownBy(() -> userService.signUp(signUpRequest)).isInstanceOf(CustomException.class);
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

        assertThatThrownBy(() -> userService.deleteUser(notExistedId)).isInstanceOf(CustomException.class);
    }

    @AfterEach
    void tearDown() {
        verificationTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
}

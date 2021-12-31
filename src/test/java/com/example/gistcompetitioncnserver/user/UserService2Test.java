package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.exception.CustomException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class UserService2Test {
    private static final String GIST_EMAIL = "tester@gist.ac.kr";
    private static final String PASSWORD = "password!";

    @Autowired
    private UserService2 userService2;
    @Autowired
    private User2Repository user2Repository;
    @Autowired
    private Encryptor encryptor;

    @ParameterizedTest
    @ValueSource(strings = {"email@gist.ac.kr","email@gm.gist.ac.kr" })
    void signUp(String email) {
        SignUpRequest signUpRequest = new SignUpRequest(email, PASSWORD);

        Long userId = userService2.signUp(signUpRequest);

        User2 user = user2Repository.findById(userId).orElseThrow(IllegalArgumentException::new);
        assertThat(userId).isNotNull();
        assertThat(user.getUsername()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(encryptor.encode(PASSWORD));
        assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void signUpFailedIfAlreadyExisted() {
        SignUpRequest signUpRequest = new SignUpRequest(GIST_EMAIL, PASSWORD);
        userService2.signUp(signUpRequest);

        assertThatThrownBy(() -> userService2.signUp(signUpRequest)).isInstanceOf(CustomException.class);
    }

    @Test
    void signUpFailedIfNotValidEmailForm() {
        String notGistEmail = "email@email.com";
        SignUpRequest signUpRequest = new SignUpRequest(notGistEmail, PASSWORD);

        assertThatThrownBy(() -> userService2.signUp(signUpRequest)).isInstanceOf(CustomException.class);
    }

    @Test
    void deleteUser() {
        SignUpRequest signUpRequest = new SignUpRequest(GIST_EMAIL, PASSWORD);
        Long userId = userService2.signUp(signUpRequest);

        userService2.deleteUser(userId);

        assertFalse(user2Repository.existsById(userId));
    }

    @Test
    void deleteUserIfNotExisted() {
        Long notExistedId = Long.MAX_VALUE;

        assertThatThrownBy(() -> userService2.deleteUser(notExistedId)).isInstanceOf(CustomException.class);
    }

    @AfterEach
    void tearDown() {
        user2Repository.deleteAllInBatch();
    }
}

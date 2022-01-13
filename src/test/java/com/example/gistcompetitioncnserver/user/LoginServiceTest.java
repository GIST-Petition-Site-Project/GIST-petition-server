package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.exception.user.NoSuchUserException;
import com.example.gistcompetitioncnserver.exception.user.NotMatchedPasswordException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LoginServiceTest {
    private static final String GIST_EMAIL = "tester@gist.ac.kr";
    private static final String PASSWORD = "password!";

    @Autowired
    private LoginService loginService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Encryptor encoder;

    @Test
    void signIn() {
        User registeredUser = userRepository.save(new User(GIST_EMAIL, encoder.hashPassword(PASSWORD), UserRole.USER));
        SignInRequest signInRequest = new SignInRequest(GIST_EMAIL, PASSWORD);

        loginService.login(signInRequest);

        SimpleUser simpleUser = loginService.getLoginUser();
        assertThat(simpleUser.getId()).isEqualTo(registeredUser.getId());
        assertThat(simpleUser.getUserRole()).isEqualTo(registeredUser.getUserRole());
    }

    @Test
    void signInFailedIfNotValidUsername() {
        userRepository.save(new User(GIST_EMAIL, encoder.hashPassword(PASSWORD), UserRole.USER));
        String fakeUsername = "wrong@gist.ac.kr";
        SignInRequest signInRequest = new SignInRequest(fakeUsername, PASSWORD);

        assertThatThrownBy(
                () -> loginService.login(signInRequest)
        ).isInstanceOf(NoSuchUserException.class);
        assertThat(loginService.getLoginUser()).isNull();
    }

    @Test
    void signInFailedIfNotValidPassword() {
        userRepository.save(new User(GIST_EMAIL, encoder.hashPassword(PASSWORD), UserRole.USER));
        String fakePassword = "wrongpassword";
        SignInRequest signInRequest = new SignInRequest(GIST_EMAIL, fakePassword);

        assertThatThrownBy(
                () -> loginService.login(signInRequest)
        ).isInstanceOf(NotMatchedPasswordException.class);
        assertThat(loginService.getLoginUser()).isNull();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }
}

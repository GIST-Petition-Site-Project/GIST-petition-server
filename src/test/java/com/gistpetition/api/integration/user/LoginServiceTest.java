package com.gistpetition.api.integration.user;

import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.exception.user.NotMatchedPasswordException;
import com.gistpetition.api.integration.IntegrationTest;
import com.gistpetition.api.user.application.LoginService;
import com.gistpetition.api.user.domain.SimpleUser;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.user.dto.request.SignInRequest;
import com.gistpetition.api.utils.password.Encoder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginServiceTest extends IntegrationTest {
    private static final String GIST_EMAIL = "tester@gist.ac.kr";
    private static final String PASSWORD = "password!";

    @Autowired
    private LoginService loginService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Encoder encoder;

    @Test
    void signIn() {
        User registeredUser = userRepository.save(new User(GIST_EMAIL, encoder.hashPassword(PASSWORD), UserRole.USER));
        SignInRequest signInRequest = new SignInRequest(GIST_EMAIL, PASSWORD);

        loginService.login(signInRequest);

        SimpleUser simpleUser = loginService.getLoginUser();
        assertThat(simpleUser.getId()).isEqualTo(registeredUser.getId());
        Assertions.assertThat(simpleUser.getUserRole()).isEqualTo(registeredUser.getUserRole());
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
}

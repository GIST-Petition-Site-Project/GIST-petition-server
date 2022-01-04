package com.example.gistcompetitioncnserver.verification;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import com.example.gistcompetitioncnserver.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class VerificationServiceTest {

    public static final String TOKEN = "token";
    @Autowired
    private VerificationService tokenService;
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    private User enabledUser;
    private User unEnabledUser;

    @BeforeEach
    void setUp() {
        enabledUser = userRepository.save(new User("enabled@gist.ac.kr", "password", UserRole.USER, true));
        unEnabledUser = userRepository.save(new User("unenable@gist.ac.kr", "password", UserRole.USER, false));
    }

    @Test
    void confirm() {
        VerificationToken validToken = tokenRepository.save(new VerificationToken(TOKEN, unEnabledUser.getId(), LocalDateTime.now().plusMinutes(10)));

        tokenService.confirm(validToken.getToken());

        User user = userRepository.findById(unEnabledUser.getId()).orElseThrow(IllegalArgumentException::new);
        assertTrue(user.isEnabled());
    }

    @Test
    void confirmFailedIfExpired() {
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(10);
        VerificationToken expiredToken = tokenRepository.save(new VerificationToken(TOKEN, unEnabledUser.getId(), pastTime));

        assertThatThrownBy(() -> tokenService.confirm(expiredToken.getToken())).isInstanceOf(CustomException.class);
    }

    @Test
    void confirmAlreadyConfirmedUser() {
        VerificationToken alreadyConfirmedToken = tokenRepository.save(new VerificationToken(TOKEN, enabledUser.getId(), LocalDateTime.now().plusMinutes(10)));

        assertThatThrownBy(() -> tokenService.confirm(alreadyConfirmedToken.getToken())).isInstanceOf(CustomException.class);
    }

    @AfterEach
    void tearDown() {
        tokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
}

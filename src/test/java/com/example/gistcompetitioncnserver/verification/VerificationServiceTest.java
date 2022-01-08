package com.example.gistcompetitioncnserver.verification;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import com.example.gistcompetitioncnserver.user.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class VerificationServiceTest {

    public static final String TOKEN = "token";
    private static final String GIST_EMAIL = "tester@gist.ac.kr";
    private static final String PASSWORD = "password!";
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private VerificationInfoRepository verificationInfoRepository;
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
        VerificationToken validToken = verificationTokenRepository.save(new VerificationToken(TOKEN, unEnabledUser.getId(), LocalDateTime.now().plusMinutes(10)));

        verificationService.confirm(validToken.getToken());

        User user = userRepository.findById(unEnabledUser.getId()).orElseThrow(IllegalArgumentException::new);
        assertTrue(user.isEnabled());
    }

    @Test
    void confirmFailedIfExpired() {
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(10);
        VerificationToken expiredToken = verificationTokenRepository.save(new VerificationToken(TOKEN, unEnabledUser.getId(), pastTime));

        assertThatThrownBy(() -> verificationService.confirm(expiredToken.getToken())).isInstanceOf(CustomException.class);
    }

    @Test
    void confirmAlreadyConfirmedUser() {
        VerificationToken alreadyConfirmedToken = verificationTokenRepository.save(new VerificationToken(TOKEN, enabledUser.getId(), LocalDateTime.now().plusMinutes(10)));

        assertThatThrownBy(() -> verificationService.confirm(alreadyConfirmedToken.getToken())).isInstanceOf(CustomException.class);
    }

    @Test
    void createEmailCode() {
        String verificationCode = verificationService.createVerificationInfo(new VerificationEmailRequest(GIST_EMAIL));
        VerificationInfo verificationInfo = verificationInfoRepository.findByVerificationCode(verificationCode).orElseThrow(IllegalArgumentException::new);

        assertThat(verificationInfo.getVerificationCode()).isEqualTo(verificationCode);
        assertThat(verificationInfo.getUsername()).isEqualTo(GIST_EMAIL);
        assertThat(verificationInfo.getCreatedAt()).isNotNull();
        assertThat(verificationInfo.getConfirmedAt()).isNull();
    }

    @Test
    void createEmailCodeFailedIfAlreadyExisted() {
        userRepository.save(new User(GIST_EMAIL, PASSWORD, UserRole.USER, true));
        Assertions.assertThatThrownBy(
                () -> verificationService.createVerificationInfo(new VerificationEmailRequest(GIST_EMAIL))
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void createEmailCodeFailedIfNotValidEmailForm() {
        String notGistEmail = "notGistEmail@gmail.com";
        Assertions.assertThatThrownBy(
                () -> verificationService.createVerificationInfo(new VerificationEmailRequest(notGistEmail))
        ).isInstanceOf(CustomException.class);
    }

    @AfterEach
    void tearDown() {
        verificationTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
}

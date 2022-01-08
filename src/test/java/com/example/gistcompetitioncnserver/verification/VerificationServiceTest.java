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

import static com.example.gistcompetitioncnserver.verification.VerificationInfo.CONFIRM_EXPIRE_MINUTE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class VerificationServiceTest {

    private static final String GIST_EMAIL = "tester@gist.ac.kr";
    private static final String PASSWORD = "password!";
    private static final String VERIFICATION_CODE = "AAAAAA";

    @Autowired
    private VerificationService verificationService;
    @Autowired
    private VerificationInfoRepository verificationInfoRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createVerificationCode() {
        String verificationCode = verificationService.createVerificationInfo(new VerificationEmailRequest(GIST_EMAIL));
        VerificationInfo verificationInfo = verificationInfoRepository.findByVerificationCode(verificationCode).orElseThrow(IllegalArgumentException::new);

        assertThat(verificationInfo.getVerificationCode()).isEqualTo(verificationCode);
        assertThat(verificationInfo.getUsername()).isEqualTo(GIST_EMAIL);
        assertThat(verificationInfo.getCreatedAt()).isNotNull();
        assertThat(verificationInfo.getConfirmedAt()).isNull();
    }

    @Test
    void createVerificationCodeFailedIfAlreadyExisted() {
        userRepository.save(new User(GIST_EMAIL, PASSWORD, UserRole.USER, true));
        Assertions.assertThatThrownBy(
                () -> verificationService.createVerificationInfo(new VerificationEmailRequest(GIST_EMAIL))
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void createVerificationCodeFailedIfNotValidEmailForm() {
        String notGistEmail = "notGistEmail@gmail.com";
        Assertions.assertThatThrownBy(
                () -> verificationService.createVerificationInfo(new VerificationEmailRequest(notGistEmail))
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void confirmVerificationCode() {
        verificationInfoRepository.save(new VerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, LocalDateTime.now(), null));

        verificationService.confirmUsername(new UsernameConfirmationRequest(GIST_EMAIL, VERIFICATION_CODE));

        VerificationInfo verificationInfo = verificationInfoRepository.findByVerificationCode(VERIFICATION_CODE).orElseThrow(IllegalArgumentException::new);
        assertThat(verificationInfo.getConfirmedAt()).isNotNull();
    }

    @Test
    void confirmVerificationCodeNotExisting() {
        verificationInfoRepository.save(new VerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, LocalDateTime.now(), null));

        String incorrectVerificationCode = VERIFICATION_CODE + "A";
        UsernameConfirmationRequest requestWithIncorrectCode = new UsernameConfirmationRequest(GIST_EMAIL, incorrectVerificationCode);
        Assertions.assertThatThrownBy(
                () -> verificationService.confirmUsername(requestWithIncorrectCode)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void confirmVerificationCodeWhenExpired() {
        LocalDateTime expiredCreatedTime = LocalDateTime.now().minusMinutes(CONFIRM_EXPIRE_MINUTE + 1);
        verificationInfoRepository.save(new VerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, expiredCreatedTime, null));

        UsernameConfirmationRequest expiredInfoRequest = new UsernameConfirmationRequest(GIST_EMAIL, VERIFICATION_CODE);
        Assertions.assertThatThrownBy(
                () -> verificationService.confirmUsername(expiredInfoRequest)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void confirmVerificationAlreadyConfirmedVerification() {
        verificationInfoRepository.save(new VerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, LocalDateTime.now().minusMinutes(1), LocalDateTime.now()));
        Assertions.assertThatThrownBy(
                () -> verificationService.confirmUsername(new UsernameConfirmationRequest(GIST_EMAIL, VERIFICATION_CODE))
        ).isInstanceOf(CustomException.class);
    }

    @AfterEach
    void tearDown() {
        verificationInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
}

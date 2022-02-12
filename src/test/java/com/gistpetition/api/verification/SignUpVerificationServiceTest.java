package com.gistpetition.api.verification;

import com.gistpetition.api.ServiceTest;
import com.gistpetition.api.exception.user.DuplicatedUserException;
import com.gistpetition.api.exception.user.InvalidEmailFormException;
import com.gistpetition.api.exception.verification.DuplicatedVerificationException;
import com.gistpetition.api.exception.verification.ExpiredVerificationCodeException;
import com.gistpetition.api.exception.verification.NoSuchVerificationInfoException;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.verification.application.signup.SignUpVerificationService;
import com.gistpetition.api.verification.domain.SignUpVerificationInfo;
import com.gistpetition.api.verification.domain.SignUpVerificationInfoRepository;
import com.gistpetition.api.verification.domain.VerificationInfo;
import com.gistpetition.api.verification.dto.UsernameConfirmationRequest;
import com.gistpetition.api.verification.dto.VerificationEmailRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SignUpVerificationServiceTest extends ServiceTest {

    private static final String GIST_EMAIL = "tester@gist.ac.kr";
    private static final String PASSWORD = "password!";
    private static final String VERIFICATION_CODE = "AAAAAA";

    @Autowired
    private SignUpVerificationService signUpVerificationService;
    @Autowired
    private SignUpVerificationInfoRepository signUpVerificationInfoRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createVerificationCode() {
        String verificationCode = signUpVerificationService.createVerificationInfo(new VerificationEmailRequest(GIST_EMAIL));
        SignUpVerificationInfo verificationInfo = signUpVerificationInfoRepository.findByVerificationCode(verificationCode).orElseThrow(IllegalArgumentException::new);

        assertThat(verificationInfo.getVerificationCode()).isEqualTo(verificationCode);
        assertThat(verificationInfo.getUsername()).isEqualTo(GIST_EMAIL);
        assertThat(verificationInfo.getCreatedAt()).isNotNull();
        assertThat(verificationInfo.getConfirmedAt()).isNull();
    }

    @Test
    void createVerificationCodeFailedIfAlreadyExisted() {
        userRepository.save(new User(GIST_EMAIL, PASSWORD, UserRole.USER, true));
        assertThatThrownBy(
                () -> signUpVerificationService.createVerificationInfo(new VerificationEmailRequest(GIST_EMAIL))
        ).isInstanceOf(DuplicatedUserException.class);
    }

    @Test
    void createVerificationCodeFailedIfNotValidEmailForm() {
        String notGistEmail = "notGistEmail@gmail.com";
        assertThatThrownBy(
                () -> signUpVerificationService.createVerificationInfo(new VerificationEmailRequest(notGistEmail))
        ).isInstanceOf(InvalidEmailFormException.class);
    }

    @Test
    void createVerificationCodeIfVerificationCodeExist() {
        signUpVerificationInfoRepository.save(new SignUpVerificationInfo(GIST_EMAIL, "BBBBBB"));

        String code = signUpVerificationService.createVerificationInfo(new VerificationEmailRequest(GIST_EMAIL));

        List<SignUpVerificationInfo> infos = signUpVerificationInfoRepository.findByUsername(GIST_EMAIL);
        assertThat(infos).hasSize(1);
        assertThat(infos.get(0).getVerificationCode()).isEqualTo(code);
    }

    @Test
    void confirmVerificationCode() {
        signUpVerificationInfoRepository.save(new SignUpVerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, LocalDateTime.now(), null));

        signUpVerificationService.confirmUsername(new UsernameConfirmationRequest(GIST_EMAIL, VERIFICATION_CODE));

        SignUpVerificationInfo verificationInfo = signUpVerificationInfoRepository.findByVerificationCode(VERIFICATION_CODE).orElseThrow(IllegalArgumentException::new);
        assertThat(verificationInfo.getConfirmedAt()).isNotNull();
    }

    @Test
    void confirmVerificationCodeNotExisting() {
        signUpVerificationInfoRepository.save(new SignUpVerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, LocalDateTime.now(), null));

        String incorrectVerificationCode = VERIFICATION_CODE + "A";
        UsernameConfirmationRequest requestWithIncorrectCode = new UsernameConfirmationRequest(GIST_EMAIL, incorrectVerificationCode);
        assertThatThrownBy(
                () -> signUpVerificationService.confirmUsername(requestWithIncorrectCode)
        ).isInstanceOf(NoSuchVerificationInfoException.class);
    }

    @Test
    void confirmVerificationCodeWhenExpired() {
        LocalDateTime expiredCreatedTime = LocalDateTime.now().minusMinutes(VerificationInfo.CONFIRM_CODE_EXPIRE_MINUTE + 1);
        signUpVerificationInfoRepository.save(new SignUpVerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, expiredCreatedTime, null));

        UsernameConfirmationRequest expiredInfoRequest = new UsernameConfirmationRequest(GIST_EMAIL, VERIFICATION_CODE);
        assertThatThrownBy(
                () -> signUpVerificationService.confirmUsername(expiredInfoRequest)
        ).isInstanceOf(ExpiredVerificationCodeException.class);
    }

    @Test
    void confirmVerificationAlreadyConfirmedVerification() {
        signUpVerificationInfoRepository.save(new SignUpVerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, LocalDateTime.now(), LocalDateTime.now()));
        assertThatThrownBy(
                () -> signUpVerificationService.confirmUsername(new UsernameConfirmationRequest(GIST_EMAIL, VERIFICATION_CODE))
        ).isInstanceOf(DuplicatedVerificationException.class);
    }

    @AfterEach
    void tearDown() {
        signUpVerificationInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
}

package com.gistpetition.api.verification;

import com.gistpetition.api.ServiceTest;
import com.gistpetition.api.exception.verification.InvalidVerificationInfoException;
import com.gistpetition.api.exception.verification.NoSuchVerificationCodeException;
import com.gistpetition.api.exception.verification.NotConfirmedVerificationCodeException;
import com.gistpetition.api.verification.application.signup.SignUpValidatorImpl;
import com.gistpetition.api.verification.domain.SignUpVerificationInfo;
import com.gistpetition.api.verification.domain.SignUpVerificationInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.gistpetition.api.verification.domain.VerificationInfo.APPLY_EXPIRE_MINUTE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SignUpValidatorImplTest extends ServiceTest {
    private static final String GIST_EMAIL = "tester@gist.ac.kr";
    private static final String VERIFICATION_CODE = "AAAAAA";

    @Autowired
    SignUpValidatorImpl signUpValidator;
    @Autowired
    SignUpVerificationInfoRepository signUpVerificationInfoRepository;

    @Test
    void checkIsVerified() {
        SignUpVerificationInfo confirmed = new SignUpVerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, LocalDateTime.now(), LocalDateTime.now());
        signUpVerificationInfoRepository.save(confirmed);

        assertThatCode(
                () -> signUpValidator.checkIsVerified(GIST_EMAIL, VERIFICATION_CODE)
        ).doesNotThrowAnyException();
    }

    @Test
    void checkIsVerifiedForNotExistingInfo() {
        String notExistingInfo = "NONONO";
        assertThatThrownBy(
                () -> signUpValidator.checkIsVerified(GIST_EMAIL, notExistingInfo)
        ).isInstanceOf(NoSuchVerificationCodeException.class);
    }

    @Test
    void checkIsVerifiedIfNotConfirmed() {
        SignUpVerificationInfo notConfirmed = new SignUpVerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, LocalDateTime.now(), null);
        signUpVerificationInfoRepository.save(notConfirmed);

        assertThatThrownBy(
                () -> signUpValidator.checkIsVerified(GIST_EMAIL, VERIFICATION_CODE)
        ).isInstanceOf(NotConfirmedVerificationCodeException.class);
    }


    @Test
    void checkIsVerifiedIfSignUpTimeOut() {
        SignUpVerificationInfo timeOutInfo = new SignUpVerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, LocalDateTime.MIN, LocalDateTime.now().minusMinutes(APPLY_EXPIRE_MINUTE));
        signUpVerificationInfoRepository.save(timeOutInfo);

        assertThatThrownBy(
                () -> signUpValidator.checkIsVerified(GIST_EMAIL, VERIFICATION_CODE)
        ).isInstanceOf(InvalidVerificationInfoException.class);
    }

    @AfterEach
    void tearDown() {
        signUpVerificationInfoRepository.deleteAllInBatch();
    }
}
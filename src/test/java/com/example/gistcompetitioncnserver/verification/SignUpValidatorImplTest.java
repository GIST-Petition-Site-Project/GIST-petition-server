package com.example.gistcompetitioncnserver.verification;

import com.example.gistcompetitioncnserver.exception.WrappedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static com.example.gistcompetitioncnserver.verification.VerificationInfo.SIGN_UP_EXPIRE_MINUTE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class SignUpValidatorImplTest {
    private static final String GIST_EMAIL = "tester@gist.ac.kr";
    private static final String VERIFICATION_CODE = "AAAAAA";

    @Autowired
    SignUpValidatorImpl signUpValidator;
    @Autowired
    VerificationInfoRepository verificationInfoRepository;

    @Test
    void checkIsVerified() {
        VerificationInfo confirmed = new VerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, LocalDateTime.now(), LocalDateTime.now());
        verificationInfoRepository.save(confirmed);

        assertThatCode(
                () -> signUpValidator.checkIsVerified(GIST_EMAIL, VERIFICATION_CODE)
        ).doesNotThrowAnyException();
    }

    @Test
    void checkIsVerifiedForNotExistingInfo() {
        String notExistingInfo = "NONONO";
        assertThatThrownBy(
                () -> signUpValidator.checkIsVerified(GIST_EMAIL, notExistingInfo)
        ).isInstanceOf(WrappedException.class);
    }

    @Test
    void checkIsVerifiedIfNotConfirmed() {
        VerificationInfo notConfirmed = new VerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, LocalDateTime.now(), null);
        verificationInfoRepository.save(notConfirmed);

        assertThatThrownBy(
                () -> signUpValidator.checkIsVerified(GIST_EMAIL, VERIFICATION_CODE)
        ).isInstanceOf(WrappedException.class);
    }


    @Test
    void checkIsVerifiedIfSignUpTimeOut() {
        VerificationInfo timeOutInfo = new VerificationInfo(null, GIST_EMAIL, VERIFICATION_CODE, LocalDateTime.MIN, LocalDateTime.now().minusMinutes(SIGN_UP_EXPIRE_MINUTE));
        verificationInfoRepository.save(timeOutInfo);

        assertThatThrownBy(
                () -> signUpValidator.checkIsVerified(GIST_EMAIL, VERIFICATION_CODE)
        ).isInstanceOf(WrappedException.class);
    }

    @AfterEach
    void tearDown() {
        verificationInfoRepository.deleteAllInBatch();
    }
}
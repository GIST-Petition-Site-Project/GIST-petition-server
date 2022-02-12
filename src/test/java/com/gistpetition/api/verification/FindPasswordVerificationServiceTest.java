package com.gistpetition.api.verification;

import com.gistpetition.api.ServiceTest;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.exception.verification.DuplicatedVerificationException;
import com.gistpetition.api.exception.verification.ExpiredVerificationCodeException;
import com.gistpetition.api.exception.verification.NoSuchVerificationInfoException;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.verification.application.password.FindPasswordVerificationService;
import com.gistpetition.api.verification.domain.PasswordVerificationInfo;
import com.gistpetition.api.verification.domain.PasswordVerificationInfoRepository;
import com.gistpetition.api.verification.dto.UsernameConfirmationRequest;
import com.gistpetition.api.verification.dto.VerificationEmailRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class FindPasswordVerificationServiceTest extends ServiceTest {

    private static final String GIST_EMAIL = "tester@gist.ac.kr";
    private static final String PASSWORD = "password!";
    public static final User USER = new User(GIST_EMAIL, PASSWORD, UserRole.USER);
    private static final String VERIFICATION_CODE = "AAAAAA";

    @Autowired
    private FindPasswordVerificationService findPasswordVerificationService;
    @Autowired
    private PasswordVerificationInfoRepository passwordVerificationInfoRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        user = userRepository.save(USER);
    }

    @Test
    void createFindPasswordVerificationCode() {
        String passwordVerificationCode = findPasswordVerificationService.createPasswordVerificationInfo(new VerificationEmailRequest(user.getUsername()));
        PasswordVerificationInfo passwordVerificationInfo = passwordVerificationInfoRepository.findByVerificationCode(passwordVerificationCode).orElseThrow(IllegalAccessError::new);

        assertThat(passwordVerificationInfo.getVerificationCode()).isEqualTo(passwordVerificationCode);
        assertThat(passwordVerificationInfo.getUsername()).isEqualTo(user.getUsername());
        assertThat(passwordVerificationInfo.getCreatedAt()).isNotNull();
        assertThat(passwordVerificationInfo.getConfirmedAt()).isNull();
    }

    @Test
    void createFindPasswordVerificationCodeFailedIfNotExisted() {
        String notGistEmail = "not" + GIST_EMAIL;
        assertThatThrownBy(
                () -> findPasswordVerificationService.createPasswordVerificationInfo(new VerificationEmailRequest(notGistEmail))
        ).isInstanceOf(NoSuchUserException.class);
    }

    @Test
    void createVerificationCodeIfVerificationCodeExist() {
        passwordVerificationInfoRepository.save(new PasswordVerificationInfo(user.getUsername(), "AAAAAA"));
        String verificationCode = findPasswordVerificationService.createPasswordVerificationInfo(new VerificationEmailRequest(user.getUsername()));

        List<PasswordVerificationInfo> passwordVerificationInfos = passwordVerificationInfoRepository.findByUsername(user.getUsername());
        assertThat(passwordVerificationInfos).hasSize(1);
        assertThat(passwordVerificationInfos.get(0).getVerificationCode()).isEqualTo(verificationCode);


    }

    @Test
    void confirmVerificationCode() {
        passwordVerificationInfoRepository.save(new PasswordVerificationInfo(user.getUsername(), VERIFICATION_CODE));
        findPasswordVerificationService.confirmUsername(new UsernameConfirmationRequest(user.getUsername(), VERIFICATION_CODE));

        PasswordVerificationInfo updatedPasswordVerificationInfo = passwordVerificationInfoRepository.findByUsernameAndVerificationCode(user.getUsername(), VERIFICATION_CODE).orElseThrow(IllegalAccessError::new);
        assertThat(updatedPasswordVerificationInfo.getConfirmedAt()).isNotNull();


    }

    @Test
    void confirmVerificationCodeNotExisting() {
        passwordVerificationInfoRepository.save(new PasswordVerificationInfo(user.getUsername(), VERIFICATION_CODE));
        String incorrectVerificationCode = VERIFICATION_CODE + "A";
        assertThatThrownBy(
                () -> findPasswordVerificationService.confirmUsername(new UsernameConfirmationRequest(user.getUsername(), incorrectVerificationCode))
        ).isInstanceOf(NoSuchVerificationInfoException.class);
    }

    @Test
    void confirmVerificationCodeWhenExpired() {
        LocalDateTime expiredCreatedTime = LocalDateTime.now().minusMinutes(PasswordVerificationInfo.CONFIRM_CODE_EXPIRE_MINUTE + 1);
        passwordVerificationInfoRepository.save(new PasswordVerificationInfo(null, user.getUsername(), VERIFICATION_CODE, expiredCreatedTime, null));
        assertThatThrownBy(
                () -> findPasswordVerificationService.confirmUsername(new UsernameConfirmationRequest(user.getUsername(), VERIFICATION_CODE))
        ).isInstanceOf(ExpiredVerificationCodeException.class);
    }

    @Test
    void confirmVerificationAlreadyConfirmedVerification() {
        passwordVerificationInfoRepository.save(new PasswordVerificationInfo(null, user.getUsername(), VERIFICATION_CODE, LocalDateTime.now(), LocalDateTime.now()));
        assertThatThrownBy(
                () -> findPasswordVerificationService.confirmUsername(new UsernameConfirmationRequest(GIST_EMAIL, VERIFICATION_CODE))
        ).isInstanceOf(DuplicatedVerificationException.class);
    }

    @AfterEach
    void tearDown() {
        passwordVerificationInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
}

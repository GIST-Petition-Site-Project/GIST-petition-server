package com.gistpetition.api.verification.application.signup;

import com.gistpetition.api.exception.verification.InvalidVerificationInfoException;
import com.gistpetition.api.exception.verification.NoSuchVerificationCodeException;
import com.gistpetition.api.exception.verification.NotConfirmedVerificationCodeException;
import com.gistpetition.api.verification.domain.SignUpVerificationInfo;
import com.gistpetition.api.verification.domain.SignUpVerificationInfoRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SignUpValidatorImpl implements SignUpValidator {
    private final SignUpVerificationInfoRepository signUpVerificationInfoRepository;

    public SignUpValidatorImpl(SignUpVerificationInfoRepository signUpVerificationInfoRepository) {
        this.signUpVerificationInfoRepository = signUpVerificationInfoRepository;
    }

    @Override
    public void checkIsVerified(String username, String verificationCode) {
        SignUpVerificationInfo verificationInfo = signUpVerificationInfoRepository.findByUsernameAndVerificationCode(username, verificationCode)
                .orElseThrow(NoSuchVerificationCodeException::new);

        if (!verificationInfo.isConfirmed()) {
            throw new NotConfirmedVerificationCodeException();
        }

        if (!verificationInfo.isConfirmationValidAt(LocalDateTime.now())) {
            throw new InvalidVerificationInfoException();
        }
    }
}

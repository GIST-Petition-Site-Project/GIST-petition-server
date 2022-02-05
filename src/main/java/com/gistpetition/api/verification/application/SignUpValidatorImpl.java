package com.gistpetition.api.verification.application;

import com.gistpetition.api.exception.verification.InvalidVerificationInfoException;
import com.gistpetition.api.exception.verification.NoSuchVerificationCodeException;
import com.gistpetition.api.exception.verification.NotConfirmedVerificationCodeException;
import com.gistpetition.api.user.application.SignUpValidator;
import com.gistpetition.api.verification.domain.SignUpVerificationInfoRepository;
import com.gistpetition.api.verification.domain.VerificationInfo;
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
        VerificationInfo verificationInfo = signUpVerificationInfoRepository.findByUsernameAndVerificationCode(username, verificationCode)
                .orElseThrow(NoSuchVerificationCodeException::new);

        if (!verificationInfo.isConfirmed()) {
            throw new NotConfirmedVerificationCodeException();
        }

        if (!verificationInfo.isValidToApply(LocalDateTime.now())) {
            throw new InvalidVerificationInfoException();
        }
    }
}

package com.example.gistcompetitioncnserver.verification;

import com.example.gistcompetitioncnserver.exception.verification.InvalidVerificationInfoException;
import com.example.gistcompetitioncnserver.exception.verification.NoSuchVerificationCodeException;
import com.example.gistcompetitioncnserver.exception.verification.NotConfirmedVerificationCodeException;
import com.example.gistcompetitioncnserver.user.SignUpValidator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SignUpValidatorImpl implements SignUpValidator {
    private final VerificationInfoRepository verificationInfoRepository;

    public SignUpValidatorImpl(VerificationInfoRepository verificationInfoRepository) {
        this.verificationInfoRepository = verificationInfoRepository;
    }

    @Override
    public void checkIsVerified(String username, String verificationCode) {
        VerificationInfo verificationInfo = verificationInfoRepository.findByUsernameAndVerificationCode(username, verificationCode)
                .orElseThrow(NoSuchVerificationCodeException::new);

        if (!verificationInfo.isConfirmed()) {
            throw new NotConfirmedVerificationCodeException();
        }

        if (!verificationInfo.isValidToSignUp(LocalDateTime.now())) {
            throw new InvalidVerificationInfoException();
        }
    }
}

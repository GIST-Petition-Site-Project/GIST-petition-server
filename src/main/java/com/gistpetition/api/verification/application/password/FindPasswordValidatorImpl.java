package com.gistpetition.api.verification.application.password;

import com.gistpetition.api.exception.verification.InvalidVerificationInfoException;
import com.gistpetition.api.exception.verification.NoSuchVerificationCodeException;
import com.gistpetition.api.exception.verification.NotConfirmedVerificationCodeException;
import com.gistpetition.api.verification.domain.PasswordVerificationInfoRepository;
import com.gistpetition.api.verification.domain.VerificationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class FindPasswordValidatorImpl implements FindPasswordValidator {

    private final PasswordVerificationInfoRepository passwordVerificationInfoRepository;

    @Override
    public void checkIsVerified(String username, String verificationCode) {
        VerificationInfo verificationInfo = passwordVerificationInfoRepository.findByUsernameAndVerificationCode(username, verificationCode)
                .orElseThrow(NoSuchVerificationCodeException::new);

        if (!verificationInfo.isConfirmed()) {
            throw new NotConfirmedVerificationCodeException();
        }

        if (!verificationInfo.isValidToApply(LocalDateTime.now())) {
            throw new InvalidVerificationInfoException();
        }
    }
}

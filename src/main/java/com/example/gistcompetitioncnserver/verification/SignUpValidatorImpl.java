package com.example.gistcompetitioncnserver.verification;

import com.example.gistcompetitioncnserver.exception.CustomException;
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
                .orElseThrow(() -> new CustomException("존재하지 않는 인증 코드입니다."));

        if(!verificationInfo.isValidToSignUp(LocalDateTime.now())) {
            throw new CustomException("유효하지 않은 회원 인증 정보입니다.");
        }
    }
}

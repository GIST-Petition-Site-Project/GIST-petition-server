package com.gistpetition.api.verification.application.password;

import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.exception.verification.DuplicatedVerificationException;
import com.gistpetition.api.exception.verification.ExpiredVerificationCodeException;
import com.gistpetition.api.exception.verification.NoSuchVerificationInfoException;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.verification.application.VerificationCodeGenerator;
import com.gistpetition.api.verification.domain.PasswordVerificationInfo;
import com.gistpetition.api.verification.domain.PasswordVerificationInfoRepository;
import com.gistpetition.api.verification.dto.UsernameConfirmationRequest;
import com.gistpetition.api.verification.dto.VerificationEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class FindPasswordVerificationService {

    private final PasswordVerificationInfoRepository passwordVerificationInfoRepository;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final UserRepository userRepository;

    @Transactional
    public String createPasswordVerificationInfo(VerificationEmailRequest request) {
        String username = request.getUsername();
        if (!userRepository.existsByUsername(username)) {
            throw new NoSuchUserException();
        }

        passwordVerificationInfoRepository.deleteByUsername(username);

        String code = verificationCodeGenerator.generate();
        passwordVerificationInfoRepository.save(new PasswordVerificationInfo(username, code));
        return code;
    }

    @Transactional
    public void confirmUsername(UsernameConfirmationRequest request) {
        String username = request.getUsername();
        String verificationCode = request.getVerificationCode();

        PasswordVerificationInfo info = passwordVerificationInfoRepository.findByUsernameAndVerificationCode(username, verificationCode)
                .orElseThrow(NoSuchVerificationInfoException::new);

        if (!info.isValidToApply(LocalDateTime.now())) {
            throw new ExpiredVerificationCodeException();
        }

        if (info.isConfirmed()) {
            throw new DuplicatedVerificationException();
        }
        info.confirm();
    }
}

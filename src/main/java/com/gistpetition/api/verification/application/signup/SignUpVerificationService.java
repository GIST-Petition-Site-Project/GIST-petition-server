package com.gistpetition.api.verification.application.signup;

import com.gistpetition.api.exception.user.DuplicatedUserException;
import com.gistpetition.api.exception.user.InvalidEmailFormException;
import com.gistpetition.api.exception.verification.DuplicatedVerificationException;
import com.gistpetition.api.exception.verification.ExpiredVerificationCodeException;
import com.gistpetition.api.exception.verification.NoSuchVerificationInfoException;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.utils.email.EmailDomain;
import com.gistpetition.api.utils.email.EmailParser;
import com.gistpetition.api.verification.application.VerificationCodeGenerator;
import com.gistpetition.api.verification.domain.SignUpVerificationInfo;
import com.gistpetition.api.verification.domain.SignUpVerificationInfoRepository;
import com.gistpetition.api.verification.dto.UsernameConfirmationRequest;
import com.gistpetition.api.verification.dto.VerificationEmailRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SignUpVerificationService {

    private final SignUpVerificationInfoRepository signUpVerificationInfoRepository;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final UserRepository userRepository;

    public SignUpVerificationService(SignUpVerificationInfoRepository signUpVerificationInfoRepository, VerificationCodeGenerator verificationCodeGenerator, UserRepository userRepository) {
        this.signUpVerificationInfoRepository = signUpVerificationInfoRepository;
        this.verificationCodeGenerator = verificationCodeGenerator;
        this.userRepository = userRepository;
    }

    @Transactional
    public String createVerificationInfo(VerificationEmailRequest request) {
        String username = request.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new DuplicatedUserException();
        }
        if (!EmailDomain.has(EmailParser.parseDomainFrom(username))) {
            throw new InvalidEmailFormException();
        }
        signUpVerificationInfoRepository.deleteByUsername(username);

        String code = verificationCodeGenerator.generate();
        signUpVerificationInfoRepository.save(new SignUpVerificationInfo(username, code));
        return code;
    }

    @Transactional
    public void confirmUsername(UsernameConfirmationRequest request) {
        String username = request.getUsername();
        String verificationCode = request.getVerificationCode();

        SignUpVerificationInfo info = signUpVerificationInfoRepository.findByUsernameAndVerificationCode(username, verificationCode)
                .orElseThrow(NoSuchVerificationInfoException::new);

        if (!info.isValidToConfirm(LocalDateTime.now())) {
            throw new ExpiredVerificationCodeException();
        }

        if (info.isConfirmed()) {
            throw new DuplicatedVerificationException();
        }
        info.confirm();
    }
}

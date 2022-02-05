package com.gistpetition.api.verification.application;

import com.gistpetition.api.exception.user.DuplicatedUserException;
import com.gistpetition.api.exception.user.InvalidEmailFormException;
import com.gistpetition.api.exception.verification.DuplicatedVerificationException;
import com.gistpetition.api.exception.verification.ExpiredVerificationCodeException;
import com.gistpetition.api.exception.verification.NoSuchVerificationInfoException;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.utils.email.EmailDomain;
import com.gistpetition.api.utils.email.EmailParser;
import com.gistpetition.api.verification.domain.VerificationInfo;
import com.gistpetition.api.verification.domain.VerificationInfoRepository;
import com.gistpetition.api.verification.dto.UsernameConfirmationRequest;
import com.gistpetition.api.verification.dto.VerificationEmailRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VerificationService {

    private final VerificationInfoRepository verificationInfoRepository;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final UserRepository userRepository;

    public VerificationService(VerificationInfoRepository verificationInfoRepository, VerificationCodeGenerator verificationCodeGenerator, UserRepository userRepository) {
        this.verificationInfoRepository = verificationInfoRepository;
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
        verificationInfoRepository.deleteByUsername(username);

        String code = verificationCodeGenerator.generate();
        verificationInfoRepository.save(new VerificationInfo(username, code));
        return code;
    }

    @Transactional
    public void confirmUsername(UsernameConfirmationRequest request) {
        String username = request.getUsername();
        String verificationCode = request.getVerificationCode();

        VerificationInfo info = verificationInfoRepository.findByUsernameAndVerificationCode(username, verificationCode)
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

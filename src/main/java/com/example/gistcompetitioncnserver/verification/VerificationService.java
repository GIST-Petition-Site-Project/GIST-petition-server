package com.example.gistcompetitioncnserver.verification;

import com.example.gistcompetitioncnserver.exception.user.DuplicatedUserException;
import com.example.gistcompetitioncnserver.exception.user.InvalidEmailFormException;
import com.example.gistcompetitioncnserver.exception.verification.DuplicatedVerificationException;
import com.example.gistcompetitioncnserver.exception.verification.ExpiredVerificationCodeException;
import com.example.gistcompetitioncnserver.exception.verification.NoSuchVerificationInfoException;
import com.example.gistcompetitioncnserver.user.EmailDomain;
import com.example.gistcompetitioncnserver.user.EmailParser;
import com.example.gistcompetitioncnserver.user.UserRepository;
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

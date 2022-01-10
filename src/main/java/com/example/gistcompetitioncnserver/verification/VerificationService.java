package com.example.gistcompetitioncnserver.verification;

import com.example.gistcompetitioncnserver.exception.WrappedException;
import com.example.gistcompetitioncnserver.exception.user.DuplicatedUserException;
import com.example.gistcompetitioncnserver.exception.user.InvalidEmailFormException;
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

        String token = verificationCodeGenerator.generate();
        verificationInfoRepository.save(new VerificationInfo(username, token));
        return token;
    }

    @Transactional
    public void confirmUsername(UsernameConfirmationRequest request) {
        String username = request.getUsername();
        String verificationCode = request.getVerificationCode();

        VerificationInfo info = verificationInfoRepository.findByUsernameAndVerificationCode(username, verificationCode)
                .orElseThrow(() -> new WrappedException("존재하지 않는 인증 정보입니다.", null));

        if (!info.isValidToConfirm(LocalDateTime.now())) {
            throw new WrappedException("만료된 인증 코드입니다.", null);
        }

        if (info.isConfirmed()) {
            throw new WrappedException("이미 인증된 정보입니다.", null);
        }
        info.confirm();
    }
}

package com.example.gistcompetitioncnserver.verification;

import com.example.gistcompetitioncnserver.exception.CustomException;
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
            throw new CustomException("이미 존재하는 회원입니다");
        }
        if (!EmailDomain.has(EmailParser.parseDomainFrom(username))) {
            throw new CustomException("유효하지 않은 이메일 형태입니다");
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
                .orElseThrow(() -> new CustomException("존재하지 않는 인증 정보입니다."));

        if (!info.isValidToConfirm(LocalDateTime.now())) {
            throw new CustomException("만료된 인증 코드입니다.");
        }

        if (info.isConfirmed()) {
            throw new CustomException("이미 인증된 정보입니다.");
        }
        info.confirm();
    }
}

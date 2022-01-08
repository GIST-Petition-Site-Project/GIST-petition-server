package com.example.gistcompetitioncnserver.verification;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.user.EmailDomain;
import com.example.gistcompetitioncnserver.user.EmailParser;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final VerificationInfoRepository verificationInfoRepository;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final UserRepository userRepository;


    public VerificationService(VerificationTokenRepository verificationTokenRepository, VerificationInfoRepository verificationInfoRepository, VerificationCodeGenerator verificationCodeGenerator, UserRepository userRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.verificationInfoRepository = verificationInfoRepository;
        this.verificationCodeGenerator = verificationCodeGenerator;
        this.userRepository = userRepository;
    }

    @Transactional
    public String createToken(User user) {
        String token = UUID.randomUUID().toString();
        verificationTokenRepository.save(new VerificationToken(token, user.getId()));
        return token;
    }

    @Transactional
    public void confirm(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException("존재하지 않는 토큰입니다."));

        if (!verificationToken.isValidAt(LocalDateTime.now())) {
            throw new CustomException("만료된 토큰입니다.");
        }

        User user = userRepository.findById(verificationToken.getUserId()).orElseThrow(() -> new CustomException("존재하지 않는 사용자입니다."));
        if (user.isEnabled()) {
            throw new CustomException("이미 인증된 사용자입니다.");
        }
        user.setEnabled();
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
}

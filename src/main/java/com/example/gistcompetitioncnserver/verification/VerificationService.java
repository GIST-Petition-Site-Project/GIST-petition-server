package com.example.gistcompetitioncnserver.verification;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VerificationService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    public VerificationService(VerificationTokenRepository verificationTokenRepository, UserRepository userRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createToken(Long userId, String token) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException("존재하지 않는 사용자입니다."));
        verificationTokenRepository.save(new VerificationToken(token, userId, 20));
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
}

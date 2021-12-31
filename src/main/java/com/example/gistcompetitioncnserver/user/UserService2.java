package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.exception.ErrorCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService2 {
    private final User2Repository user2Repository;
    private final Encryptor encryptor;

    public UserService2(User2Repository user2Repository, Encryptor encryptor) {
        this.user2Repository = user2Repository;
        this.encryptor = encryptor;
    }

    @Transactional
    public Long signUp(SignUpRequest request) {
        String username = request.getUsername();
        if (user2Repository.existsByUsername(username)) {
            throw new CustomException("이미 존재하는 회원입니다");
        }
        if (!EmailDomain.has(EmailParser.parseDomainFrom(username))) {
            throw new CustomException("유효하지 않은 이메일 형태입니다");
        }

        User2 user = new User2(
                username,
                encryptor.encode(request.getPassword()),
                UserRole.USER);
        return user2Repository.save(user).getId();
    }

    // TODO: 유저 유효성 부분 구현  필요
//        if (!user.isEnabled()) {
//            throw new CustomException(ErrorCase.NO_SUCH_VERIFICATION_EMAIL_ERROR);
//        }
    public User2 findUserById(Long userId) {
        return user2Repository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCase.NO_SUCH_USER_ERROR));
    }

    @Transactional(readOnly = true)
    public User2 findUserByEmail(String email) {
        return user2Repository.findByUsername(email)
                .orElseThrow(() -> new CustomException(ErrorCase.NO_SUCH_USER_ERROR));
    }

    @Transactional(readOnly = true)
    public List<User2> findAllUsers() {
        return user2Repository.findAll();
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!user2Repository.existsById(userId)) {
            throw new CustomException("존재하지 않는 유저입니다");
        }
        user2Repository.deleteById(userId);
    }
}

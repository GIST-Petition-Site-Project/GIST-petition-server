package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.emailsender.EmailSender;
import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.exception.ErrorCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final EmailSender emailSender;
    private final Encryptor encryptor;

    public UserService(UserRepository userRepository, VerificationService verificationService, EmailSender emailSender, Encryptor encryptor) {
        this.userRepository = userRepository;
        this.verificationService = verificationService;
        this.emailSender = emailSender;
        this.encryptor = encryptor;
    }

    @Transactional
    public Long signUp(SignUpRequest request) {
        String username = request.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new CustomException("이미 존재하는 회원입니다");
        }
        if (!EmailDomain.has(EmailParser.parseDomainFrom(username))) {
            throw new CustomException("유효하지 않은 이메일 형태입니다");
        }

        User user = new User(username, encryptor.hashPassword(request.getPassword()), UserRole.USER);
        String token = verificationService.createToken(user);
        emailSender.send(user.getUsername(), token);
        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCase.NO_SUCH_USER_ERROR));
    }

    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return userRepository.findByUsername(email)
                .orElseThrow(() -> new CustomException(ErrorCase.NO_SUCH_USER_ERROR));
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException("존재하지 않는 유저입니다");
        }
        userRepository.deleteById(userId);
    }
}

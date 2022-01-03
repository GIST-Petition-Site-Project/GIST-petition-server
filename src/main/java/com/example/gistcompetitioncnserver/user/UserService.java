package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.exception.ErrorCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BcryptEncoder encoder;
    private final HttpSession httpSession;

    public UserService(UserRepository userRepository, BcryptEncoder encoder, HttpSession httpSession) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.httpSession = httpSession;
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

        User user = new User(
                username,
                encoder.hashPassword(request.getPassword()),
                UserRole.USER);
        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public void signIn(SignInRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException("존재하지 않는 회원 입니다."));
        if (!encoder.isMatch(request.getPassword(), user.getPassword())) {
            throw new CustomException("비밀번호를 다시 확인해주세요");
        }
        httpSession.setAttribute("user", new SessionUser(user));
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

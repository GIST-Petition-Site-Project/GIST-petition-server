package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.exception.user.NoSuchUserException;
import com.example.gistcompetitioncnserver.exception.user.NotMatchedPasswordException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@Service
@AllArgsConstructor
public class SessionLoginService implements LoginService {
    public static final String SESSION_KEY = "user";
    private final HttpSession httpSession;
    private final UserRepository userRepository;
    private final Encryptor encryptor;

    @Override
    @Transactional
    public void login(SignInRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(NoSuchUserException::new);
        if (!encryptor.isMatch(request.getPassword(), user.getPassword())) {
            throw new NotMatchedPasswordException();
        }
        httpSession.setAttribute(SESSION_KEY, new SessionUser(user));
    }

    @Override
    public void logout() {
        httpSession.invalidate();
    }

    @Override
    public LoginUser getLoginUser() {
        return (SessionUser) httpSession.getAttribute(SESSION_KEY);
    }
}

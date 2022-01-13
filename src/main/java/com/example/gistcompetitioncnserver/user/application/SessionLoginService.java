package com.example.gistcompetitioncnserver.user.application;

import com.example.gistcompetitioncnserver.common.password.Encoder;
import com.example.gistcompetitioncnserver.exception.user.NoSuchUserException;
import com.example.gistcompetitioncnserver.exception.user.NotMatchedPasswordException;
import com.example.gistcompetitioncnserver.user.domain.SimpleUser;
import com.example.gistcompetitioncnserver.user.domain.User;
import com.example.gistcompetitioncnserver.user.domain.UserRepository;
import com.example.gistcompetitioncnserver.user.dto.request.SignInRequest;
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
    private final Encoder encoder;

    @Override
    @Transactional
    public void login(SignInRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(NoSuchUserException::new);
        if (!encoder.isMatch(request.getPassword(), user.getPassword())) {
            throw new NotMatchedPasswordException();
        }
        httpSession.setAttribute(SESSION_KEY, new SimpleUser(user));
    }

    @Override
    public void logout() {
        httpSession.invalidate();
    }

    @Override
    public SimpleUser getLoginUser() {
        return (SimpleUser) httpSession.getAttribute(SESSION_KEY);
    }
}

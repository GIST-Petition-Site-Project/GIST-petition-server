package com.gistpetition.api.user.application;

import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.exception.user.NotMatchedPasswordException;
import com.gistpetition.api.user.domain.SimpleUser;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.dto.request.SignInRequest;
import com.gistpetition.api.utils.password.Encoder;
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
    @Transactional(readOnly = true)
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

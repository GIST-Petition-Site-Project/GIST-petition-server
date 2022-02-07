package com.gistpetition.api.config;

import com.gistpetition.api.user.domain.SimpleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Optional;

import static com.gistpetition.api.user.application.SessionLoginService.SESSION_KEY;

@RequiredArgsConstructor
@Component
public class Auditor implements AuditorAware<Long> {
    private final HttpSession httpSession;

    @Override
    public Optional<Long> getCurrentAuditor() {
        SimpleUser user = (SimpleUser) httpSession.getAttribute(SESSION_KEY);
        if (user == null)
            return Optional.empty();
        return Optional.ofNullable(user.getId());
    }
}

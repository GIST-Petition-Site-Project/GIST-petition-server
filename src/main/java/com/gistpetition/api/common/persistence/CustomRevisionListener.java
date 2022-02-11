package com.gistpetition.api.common.persistence;

import com.gistpetition.api.user.domain.SimpleUser;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.RevisionListener;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Objects;

import static com.gistpetition.api.user.application.SessionLoginService.SESSION_KEY;

@RequiredArgsConstructor
@Component
public class CustomRevisionListener implements RevisionListener {

    private final HttpSession httpSession;

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity exampleRevEntity = (CustomRevisionEntity) revisionEntity;
        exampleRevEntity.setUserId(extractUserId());
    }

    private Long extractUserId() {
        SimpleUser user = (SimpleUser) httpSession.getAttribute(SESSION_KEY);
        return Objects.isNull(user) ? null : user.getId();
    }
}

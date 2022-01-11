package com.example.gistcompetitioncnserver.config;


import com.example.gistcompetitioncnserver.config.annotation.ManagerPermissionRequired;
import com.example.gistcompetitioncnserver.exception.user.UnAuthenticatedException;
import com.example.gistcompetitioncnserver.exception.user.UnAuthorizedUserException;
import com.example.gistcompetitioncnserver.user.SessionUser;
import com.example.gistcompetitioncnserver.user.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class ManagerPermissionInterceptor implements HandlerInterceptor {
    private final UserService userService;

    public ManagerPermissionInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod && ((HandlerMethod) handler).hasMethodAnnotation(ManagerPermissionRequired.class)) {
            SessionUser sessionUser = userService.getSessionUser();
            if (Objects.isNull(sessionUser)) {
                throw new UnAuthenticatedException();
            }
            if (!sessionUser.hasManagerAuthority()) {
                throw new UnAuthorizedUserException();
            }
            return true;
        }
        return false;
    }
}

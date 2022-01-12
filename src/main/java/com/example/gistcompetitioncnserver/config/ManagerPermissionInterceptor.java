package com.example.gistcompetitioncnserver.config;


import com.example.gistcompetitioncnserver.config.annotation.ManagerPermissionRequired;
import com.example.gistcompetitioncnserver.exception.user.UnAuthenticatedException;
import com.example.gistcompetitioncnserver.exception.user.UnAuthorizedUserException;
import com.example.gistcompetitioncnserver.user.LoginService;
import com.example.gistcompetitioncnserver.user.SimpleUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
@AllArgsConstructor
public class ManagerPermissionInterceptor implements HandlerInterceptor {
    private final LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod && ((HandlerMethod) handler).hasMethodAnnotation(ManagerPermissionRequired.class)) {
            SimpleUser simpleUser = loginService.getLoginUser();
            if (Objects.isNull(simpleUser)) {
                throw new UnAuthenticatedException();
            }
            if (!simpleUser.hasManagerAuthority()) {
                throw new UnAuthorizedUserException();
            }
        }
        return true;
    }
}

package com.gistpetition.api.config.interceptor;


import com.gistpetition.api.config.annotation.ManagerPermissionRequired;
import com.gistpetition.api.exception.user.UnAuthenticatedException;
import com.gistpetition.api.exception.user.UnAuthorizedUserException;
import com.gistpetition.api.user.application.LoginService;
import com.gistpetition.api.user.domain.SimpleUser;
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

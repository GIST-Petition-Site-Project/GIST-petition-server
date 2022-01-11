package com.example.gistcompetitioncnserver.config;


import com.example.gistcompetitioncnserver.config.annotation.LoginRequired;
import com.example.gistcompetitioncnserver.exception.user.UnAuthenticatedException;
import com.example.gistcompetitioncnserver.user.LoginService;
import com.example.gistcompetitioncnserver.user.LoginUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
@AllArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
    private final LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod && ((HandlerMethod) handler).hasMethodAnnotation(LoginRequired.class)) {
            LoginUser loginUser = loginService.getLoginUser();
            if (Objects.isNull(loginUser)) {
                throw new UnAuthenticatedException();
            }
            return true;
        }
        return false;
    }
}

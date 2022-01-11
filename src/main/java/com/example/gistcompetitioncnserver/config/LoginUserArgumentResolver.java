package com.example.gistcompetitioncnserver.config;

import com.example.gistcompetitioncnserver.config.annotation.LoginUser;
import com.example.gistcompetitioncnserver.user.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@AllArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final LoginService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.getParameterAnnotation(LoginUser.class) == null) {
            return false;
        }
        return parameter.getParameterType().equals(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return userService.getLoginUser();
    }
}

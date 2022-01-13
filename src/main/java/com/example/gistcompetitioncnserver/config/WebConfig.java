package com.example.gistcompetitioncnserver.config;

import com.example.gistcompetitioncnserver.config.argumentresolver.LoginUserArgumentResolver;
import com.example.gistcompetitioncnserver.config.interceptor.AdminPermissionInterceptor;
import com.example.gistcompetitioncnserver.config.interceptor.LoginInterceptor;
import com.example.gistcompetitioncnserver.config.interceptor.ManagerPermissionInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final LoginInterceptor loginInterceptor;
    private final ManagerPermissionInterceptor managerPermissionInterceptor;
    private final AdminPermissionInterceptor adminPermissionInterceptor;
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://localhost:3000",
                        "https://127.0.0.1:3000",
                        "https://dev.gist-petition.com")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor);
        registry.addInterceptor(managerPermissionInterceptor);
        registry.addInterceptor(adminPermissionInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
    }
}

package com.gistpetition.api.config;

import ch.qos.logback.access.servlet.TeeFilter;
import com.gistpetition.api.config.argumentresolver.LoginUserArgumentResolver;
import com.gistpetition.api.config.interceptor.AdminPermissionInterceptor;
import com.gistpetition.api.config.interceptor.LoginInterceptor;
import com.gistpetition.api.config.interceptor.ManagerPermissionInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    @Value("${request.origins:https://localhost:8080}")
    String[] origins;

    private final LoginInterceptor loginInterceptor;
    private final ManagerPermissionInterceptor managerPermissionInterceptor;
    private final AdminPermissionInterceptor adminPermissionInterceptor;
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders(HttpHeaders.LOCATION)
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

    @Bean
    public FilterRegistrationBean requestLoggingFilter() {
        return new FilterRegistrationBean(new TeeFilter());
    }
}

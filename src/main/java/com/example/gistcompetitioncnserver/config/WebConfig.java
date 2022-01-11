package com.example.gistcompetitioncnserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000",
                        "http://127.0.0.1:3000",
                        "http://petition-bucket.s3-website.ap-northeast-2.amazonaws.com")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

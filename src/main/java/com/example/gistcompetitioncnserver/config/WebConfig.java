package com.example.gistcompetitioncnserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://quizzical-williams-59e489.netlify.app/", "https://gist-petition-web-b3oa5a455-better-it.vercel.app/")
                .allowedMethods("*");
    }
}
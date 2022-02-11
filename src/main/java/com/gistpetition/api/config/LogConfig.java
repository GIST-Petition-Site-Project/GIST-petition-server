package com.gistpetition.api.config;

import ch.qos.logback.access.servlet.TeeFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!prod")
@Configuration
public class LogConfig {

    @Bean
    public FilterRegistrationBean teeFilter() {
        TeeFilter teeFilter = new TeeFilter();
        return new FilterRegistrationBean(teeFilter);
    }
}

package com.gistpetition.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@EnableEnversRepositories(basePackages = "com.gistpetition.api")
@Configuration
public class JpaConfiguration {
}

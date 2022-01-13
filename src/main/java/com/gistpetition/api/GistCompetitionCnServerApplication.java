package com.gistpetition.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GistCompetitionCnServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GistCompetitionCnServerApplication.class, args);
    }
}

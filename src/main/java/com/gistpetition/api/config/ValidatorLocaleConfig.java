package com.gistpetition.api.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Locale;

@Configuration
public class ValidatorLocaleConfig {
    @Bean
    public Validator validator() {
        return Validation.byProvider(HibernateValidator.class).
                configure().
                defaultLocale(Locale.KOREA).
                buildValidatorFactory().getValidator();
    }
}

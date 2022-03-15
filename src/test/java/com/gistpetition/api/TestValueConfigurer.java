package com.gistpetition.api;

import com.gistpetition.api.petition.domain.Petition;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TestValueConfigurer {
    @PostConstruct
    private void init() {
        Petition.REQUIRED_AGREEMENT_FOR_RELEASE = 2;
        Petition.REQUIRED_AGREEMENT_FOR_ANSWER = 5;
    }
}

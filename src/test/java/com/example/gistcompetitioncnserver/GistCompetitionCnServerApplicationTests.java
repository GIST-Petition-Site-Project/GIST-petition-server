package com.example.gistcompetitioncnserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;


@SpringBootTest
class GistCompetitionCnServerApplicationTests {
    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Test
    @DisplayName("템플릿 엔진이 적용되는지 확인하는 테스트")
    void templateEngineTest() {
        Context context = new Context();
        context.setVariable("confirmUrl", "https://www.naver.com");
        String body = springTemplateEngine.process("email-verification.html", context);
        System.out.println("body = " + body);
    }
}

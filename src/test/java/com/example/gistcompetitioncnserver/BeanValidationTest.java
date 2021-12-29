package com.example.gistcompetitioncnserver;

import com.example.gistcompetitioncnserver.post.PostRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@SpringBootTest
public class BeanValidationTest {


    @Test
    void beanValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        PostRequest postRequest = new PostRequest("청원합니다", "이러이러하다", "분류1");
        Set<ConstraintViolation<PostRequest>> validate = validator.validate(postRequest);
        System.out.println(validate);
    }

    @Test
    void beanValidation2() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        PostRequest postRequest = new PostRequest(null, "이러이러하다", "분류1");
        Set<ConstraintViolation<PostRequest>> validate = validator.validate(postRequest);
        System.out.println(validate);
    }
}

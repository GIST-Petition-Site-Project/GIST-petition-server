package com.gistpetition.api.acceptance.validation;

import com.gistpetition.api.acceptance.AcceptanceTest;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.utils.emailsender.EmailSender;
import com.gistpetition.api.verification.application.FixedVerificationCodeGenerator;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static com.gistpetition.api.acceptance.common.TUser.GUNE;
import static org.assertj.core.api.Assertions.assertThat;

public class ValidationTest extends AcceptanceTest {

    @Test
    @DisplayName("SignUpRequest validation - 잘못된 Email 폼 등록")
    void signUpRequest() {
        String wrongFormatUsername = "lee.gist.ac.kr";
        GUNE.getVerificationCodeWith(GUNE.getUsername());
        GUNE.confirmVerificationCodeWith(GUNE.getUsername(), FixedVerificationCodeGenerator.FIXED_VERIFICATION_CODE);
        Response register = GUNE.doRegisterWith(
                wrongFormatUsername,
                GUNE.getPassword(),
                FixedVerificationCodeGenerator.FIXED_VERIFICATION_CODE
        );
        assertThat(register.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("청원 생성 실패 - 빈 제목")
    void createPetitionRequest() {
        String emptyTitle = "";
        GUNE.doSignUp();
        Response createPetition = GUNE.doLoginAndThen().createPetitionWith(emptyTitle, "description", Category.ETC.getId());
        assertThat(createPetition.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}

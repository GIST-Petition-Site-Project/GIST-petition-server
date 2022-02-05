package com.gistpetition.api.acceptableTest;

import com.gistpetition.api.petition.domain.PetitionRepository;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.dto.request.SignInRequest;
import com.gistpetition.api.user.dto.request.SignUpRequest;
import com.gistpetition.api.verification.domain.VerificationInfoRepository;
import com.gistpetition.api.verification.dto.UsernameConfirmationRequest;
import com.gistpetition.api.verification.dto.VerificationEmailRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAPITest {
    @Autowired
    PetitionRepository petitionRepository;

    @Autowired
    VerificationInfoRepository verificationInfoRepository;

    @Autowired
    UserRepository userRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    void setPort() {
        RestAssured.port = port;
    }

    @Test
    void signupAPIs() {
        String username = "testUsername@gm.gist.ac.kr";
        String password = "testPassword";
        String verificationCode = "AAAAAA";

        VerificationEmailRequest verificationEmailRequest = new VerificationEmailRequest(username);
        Response createVerificationCode = given().
                contentType(ContentType.JSON).
                body(verificationEmailRequest).
                when().
                post("/v1/username/verifications");
        assertThat(createVerificationCode.getStatusCode()).isEqualTo(204);

        UsernameConfirmationRequest usernameConfirmationRequest = new UsernameConfirmationRequest(username, verificationCode);
        Response confirmVerificationCode = given().
                contentType(ContentType.JSON).
                body(usernameConfirmationRequest).
                when().
                post("/v1/username/confirm");
        assertThat(confirmVerificationCode.getStatusCode()).isEqualTo(204);

        SignUpRequest signUpRequest = new SignUpRequest(username, password, verificationCode);
        Response register = given().
                contentType(ContentType.JSON).
                body(signUpRequest).
                when().
                post("/v1/users");
        assertThat(register.getStatusCode()).isEqualTo(201);
        assertThat(register.getHeader("Location")).contains("/users/");
    }

    @Test
    void loginAPIs() {
        String username = "admin@gist.ac.kr";
        String password = "test1234!";

        SignInRequest signInRequest = new SignInRequest(username, password);
        Response login = given().
                contentType(ContentType.JSON).
                body(signInRequest).
                when().
                post("/v1/login");
        assertThat(login.getStatusCode()).isEqualTo(204);
    }
}

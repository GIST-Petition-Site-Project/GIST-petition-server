package com.gistpetition.api.acceptance.common;

import com.gistpetition.api.FixedVerificationCodeGenerator;
import com.gistpetition.api.user.dto.request.SignInRequest;
import com.gistpetition.api.user.dto.request.SignUpRequest;
import com.gistpetition.api.verification.dto.UsernameConfirmationRequest;
import com.gistpetition.api.verification.dto.VerificationEmailRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public enum TUser {
    T_ADMIN("testAdmin@gist.ac.kr", "admin"),
    GUNE("gune@gm.gist.ac.kr", "gune"),
    EUNGI("handsomeGuy@gm.gist.ac.kr", "It's me!"),
    WANNTE("wannte@gm.gist.ac.kr", "wannte"),
    KOSE("kose@gist.ac.kr", "kose");

    private final String username;
    private final String password;

    private Long id;
    private String jSessionId;

    TUser(String username, String password) {
        this.username = username;
        this.password = password;
        this.id = -1L;
        this.jSessionId = "";
    }

    public static void clearAll() {
        for (TUser tUser : values()) {
            tUser.clear();
        }
    }

    public void clear() {
        this.id = -1L;
        this.jSessionId = "";
    }

    public void doSignUp() {
        VerificationEmailRequest verificationEmailRequest = new VerificationEmailRequest(username);
        given().
                contentType(ContentType.JSON).
                body(verificationEmailRequest).
                when().
                post("/v1/sign-up/verifications").
                then().log().all().
                statusCode(HttpStatus.NO_CONTENT.value());
        String verificationCode = FixedVerificationCodeGenerator.FIXED_VERIFICATION_CODE;

        UsernameConfirmationRequest usernameConfirmationRequest = new UsernameConfirmationRequest(username, verificationCode);
        given().
                contentType(ContentType.JSON).
                body(usernameConfirmationRequest).
                when().
                post("/v1/sign-up/confirm").
                then().log().all().
                statusCode(HttpStatus.NO_CONTENT.value());

        SignUpRequest signUpRequest = new SignUpRequest(username, password, verificationCode);
        String location = given().
                contentType(ContentType.JSON).
                body(signUpRequest).
                when().
                post("/v1/users").
                then().log().all().
                statusCode(HttpStatus.CREATED.value()).
                header(HttpHeaders.LOCATION, containsString("/users/")).
                extract().header(HttpHeaders.LOCATION);
        id = Long.valueOf(location.substring(7));
    }

    public void doLogin() {
        SignInRequest signInRequest = new SignInRequest(username, password);
        Response login = given().
                contentType(ContentType.JSON).
                body(signInRequest).
                when().
                post("/v1/login").
                then().log().all().
                statusCode(HttpStatus.NO_CONTENT.value()).extract().response();
        this.jSessionId = login.cookie("JSESSIONID");
    }

    public LoginAndThenAct doLoginAndThen() {
        this.doLogin();
        return new LoginAndThenAct(this);
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public Long getId() {
        return this.id;
    }

    public String getJSessionId() {
        return this.jSessionId;
    }

}

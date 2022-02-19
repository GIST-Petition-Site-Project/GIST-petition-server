package com.gistpetition.api.acceptance.common;

import com.gistpetition.api.user.dto.request.SignInRequest;
import com.gistpetition.api.user.dto.request.SignUpRequest;
import com.gistpetition.api.verification.application.FixedVerificationCodeGenerator;
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
    T_MANAGER("testManager@gist.ac.kr", "manager"),
    GUNE("gune@gm.gist.ac.kr", "gune"),
    EUNGI("handsomeGuy@gm.gist.ac.kr", "It's me!"),
    WANNTE("wannte@gm.gist.ac.kr", "wannte"),
    KOSE("kose@gist.ac.kr", "kose"),
    AGREE_USER1("agree1@gist.ac.kr", "agree1"),
    AGREE_USER2("agree2@gist.ac.kr", "agree2"),
    AGREE_USER3("agree3@gist.ac.kr", "agree3"),
    AGREE_USER4("agree4@gist.ac.kr", "agree4"),
    AGREE_USER5("agree5@gist.ac.kr", "agree5"),
    ;

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
        doSignUpWith(username, password);
    }

    public void doLogin() {
        Response login = doLoginWith(username, password).
                then().
                statusCode(HttpStatus.NO_CONTENT.value()).extract().response();
        this.jSessionId = login.cookie("JSESSIONID");
    }

    public LoginAndThenAct doLoginAndThen() {
        this.doLogin();
        return new LoginAndThenAct(this);
    }

    public Response doLoginWith(String username, String password) {
        SignInRequest signInRequest = new SignInRequest(username, password);
        return given().
                contentType(ContentType.JSON).
                body(signInRequest).
                when().
                post("/v1/login");
    }

    public void doSignUpWith(String username, String password) {
        getVerificationCodeWith(username).
                then().
                statusCode(HttpStatus.NO_CONTENT.value());
        String verificationCode = FixedVerificationCodeGenerator.FIXED_VERIFICATION_CODE;

        confirmVerificationCodeWith(username, verificationCode).
                then().
                statusCode(HttpStatus.NO_CONTENT.value());

        String[] location = doRegisterWith(username, password, verificationCode).
                then().
                statusCode(HttpStatus.CREATED.value()).
                header(HttpHeaders.LOCATION, containsString("/users/")).
                extract().header(HttpHeaders.LOCATION).split("/");
        id = Long.valueOf(location[location.length - 1]);
    }


    public Response getVerificationCodeWith(String username) {
        VerificationEmailRequest verificationEmailRequest = new VerificationEmailRequest(username);
        return given().
                contentType(ContentType.JSON).
                body(verificationEmailRequest).
                when().
                post("/v1/sign-up/verifications");
    }

    public Response confirmVerificationCodeWith(String username, String verificationCode) {
        UsernameConfirmationRequest usernameConfirmationRequest = new UsernameConfirmationRequest(username, verificationCode);
        return given().
                contentType(ContentType.JSON).
                body(usernameConfirmationRequest).
                when().
                post("/v1/sign-up/confirm");
    }

    public Response doRegisterWith(String username, String password, String verificationCode) {
        SignUpRequest signUpRequest = new SignUpRequest(username, password, verificationCode);
        return given().
                contentType(ContentType.JSON).
                body(signUpRequest).
                when().
                post("/v1/users");
    }

    public LoginAndThenAct doAct() {
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

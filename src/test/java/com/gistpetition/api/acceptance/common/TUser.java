package com.gistpetition.api.acceptance.common;

import com.gistpetition.api.user.domain.UserRole;
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
    ADMIN("testAdmin@gist.ac.kr", "admin", UserRole.ADMIN, "AAAAAA"),
    MANAGER("testManager@gist.ac.kr", "manager", UserRole.MANAGER, "AAAAAA"),
    NORMAL("testNormal@gist.ac.kr", "normal", UserRole.USER, "AAAAAA");

    private final String username;
    private final String password;
    private final String verificationCode;
    private final UserRole userRole;
    private Long id;
    private String jSessionId;

    TUser(String username, String password, UserRole userRole, String verificationCode) {
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.verificationCode = verificationCode;
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
                post("/v1/username/verifications").
                then().log().all().
                statusCode(HttpStatus.NO_CONTENT.value());

        UsernameConfirmationRequest usernameConfirmationRequest = new UsernameConfirmationRequest(username, verificationCode);
        given().
                contentType(ContentType.JSON).
                body(usernameConfirmationRequest).
                when().
                post("/v1/username/confirm").
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
        jSessionId = login.cookie("JSESSIONID");
    }

    public LoginAndThenAct doLoginAndThen() {
        this.doLogin();
        return new LoginAndThenAct(this);
    }

    public UserRole getUserRole() {
        return this.userRole;
    }

    public Long getId() {
        return this.id;
    }

    public String getJSessionId() {
        return this.jSessionId;
    }
}

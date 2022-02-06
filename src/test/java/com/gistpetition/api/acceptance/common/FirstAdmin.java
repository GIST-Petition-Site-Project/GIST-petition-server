package com.gistpetition.api.acceptance.common;

import com.gistpetition.api.user.dto.request.SignInRequest;
import com.gistpetition.api.user.dto.request.UpdateUserRoleRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

public enum FirstAdmin {
    FIRST_ADMIN("admin@gist.ac.kr", "test1234!");

    private static String JSESSIONID = "";
    private final String username;
    private final String password;

    public void clear() {
        JSESSIONID = "";
    }

    FirstAdmin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public FirstAdmin login() {
        SignInRequest signInRequest = new SignInRequest(username, password);
        Response login = given().
                contentType(ContentType.JSON).
                body(signInRequest).
                when().
                post("/v1/login").
                then().log().all().
                statusCode(HttpStatus.NO_CONTENT.value()).extract().response();
        JSESSIONID = login.cookie("JSESSIONID");
        return this;
    }

    public void updateUserRole(TUser tUser) {
        UpdateUserRoleRequest updateUserRoleRequest = new UpdateUserRoleRequest(tUser.getUserRole().name());
        given().
                cookie("JSESSIONID", JSESSIONID).
                contentType(ContentType.JSON).
                body(updateUserRoleRequest).
                when().
                put("/v1/users/" + tUser.getId() + "/userRole").
                then().log().all().
                statusCode(HttpStatus.NO_CONTENT.value()).extract().response();
    }

}

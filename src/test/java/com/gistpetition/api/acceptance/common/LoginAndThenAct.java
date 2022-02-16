package com.gistpetition.api.acceptance.common;

import com.gistpetition.api.petition.dto.PetitionRequest;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.user.dto.request.UpdateUserRoleRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class LoginAndThenAct {
    private final TUser tUser;

    LoginAndThenAct(TUser tUser) {
        this.tUser = tUser;
    }

    public Response createPetitionWith(String title, String description, Long categoryId) {
        PetitionRequest petitionRequest = new PetitionRequest(title, description, categoryId);
        return createPetition(petitionRequest);
    }

    public Response createPetition(PetitionRequest petitionRequest) {
        return given().
                contentType(ContentType.JSON).
                cookie("JSESSIONID", tUser.getJSessionId()).
                body(petitionRequest).
                when().
                post("/v1/petitions");
    }

    public LoginAndThenAct createPetitionAndThen(PetitionRequest petitionRequest) {
        given().
                contentType(ContentType.JSON).
                cookie("JSESSIONID", tUser.getJSessionId()).
                body(petitionRequest).
                when().
                post("/v1/petitions").
                then().
                statusCode(HttpStatus.CREATED.value());
        return this;
    }

    public LoginAndThenAct updateUserRoleAndThen(TUser target, UserRole userRole) {
        UpdateUserRoleRequest updateUserRoleRequest = new UpdateUserRoleRequest(userRole.name());
        given().
                cookie("JSESSIONID", tUser.getJSessionId()).
                contentType(ContentType.JSON).
                body(updateUserRoleRequest).
                when().
                put("/v1/users/" + target.getId() + "/userRole").
                then().
                statusCode(HttpStatus.NO_CONTENT.value()).extract().response();
        return this;
    }
}

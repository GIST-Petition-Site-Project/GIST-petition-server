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

    public Response createPetition(PetitionRequest petitionRequest) {
        return given().
                contentType(ContentType.JSON).
                cookie("JSESSIONID", tUser.getJSessionId()).
                body(petitionRequest).
                when().
                post("/v1/petitions").
                then().log().all().
                statusCode(HttpStatus.CREATED.value()).extract().response();
    }

    public void updateUserRole(TUser target, UserRole userRole) {
        UpdateUserRoleRequest updateUserRoleRequest = new UpdateUserRoleRequest(userRole.name());
        given().
                cookie("JSESSIONID", tUser.getJSessionId()).
                contentType(ContentType.JSON).
                body(updateUserRoleRequest).
                when().
                put("/v1/users/" + target.getId() + "/userRole").
                then().log().all().
                statusCode(HttpStatus.NO_CONTENT.value()).extract().response();
    }
}

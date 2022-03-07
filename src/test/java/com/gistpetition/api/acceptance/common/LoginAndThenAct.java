package com.gistpetition.api.acceptance.common;

import com.gistpetition.api.petition.dto.request.AgreementRequest;
import com.gistpetition.api.petition.dto.request.AnswerRequest;
import com.gistpetition.api.petition.dto.request.PetitionRequest;
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

    public Response createAnswer(Long petitionId, AnswerRequest answerRequest) {
        return given().
                contentType(ContentType.JSON).
                cookie("JSESSIONID", tUser.getJSessionId()).
                body(answerRequest).
                when().
                post("/v1/petitions/" + petitionId + "/answer");
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

    public Response retrieveTempPetition(String tempUrl) {
        return given().
                cookie("JSESSIONID", tUser.getJSessionId()).
                when().
                get("/v1/petitions/temp/" + tempUrl);
    }

    public Response retrieveReleasedPetition(Long petitionId) {
        return given().
                cookie("JSESSIONID", tUser.getJSessionId()).
                when().
                get("/v1/petitions/" + petitionId);
    }

    public Response agreePetitionWith(AgreementRequest agreementRequest, Long petitionId) {
        return given().
                contentType(ContentType.JSON).
                cookie("JSESSIONID", tUser.getJSessionId()).
                body(agreementRequest).
                when().
                post("/v1/petitions/" + petitionId + "/agreements").
                then().
                statusCode(HttpStatus.OK.value()).extract().response();
    }

    public Response releasePetition(Long petitionId) {
        return given().
                contentType(ContentType.JSON).
                cookie("JSESSIONID", tUser.getJSessionId()).
                when().
                post("/v1/petitions/" + petitionId + "/release").
                then().
                statusCode(HttpStatus.NO_CONTENT.value()).extract().response();
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

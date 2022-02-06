package com.gistpetition.api.acceptance.common;

import com.gistpetition.api.petition.dto.PetitionRequest;
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
}

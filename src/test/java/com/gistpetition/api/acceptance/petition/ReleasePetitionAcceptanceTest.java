package com.gistpetition.api.acceptance.petition;

import com.gistpetition.api.acceptance.AcceptanceTest;
import com.gistpetition.api.acceptance.common.TUser;
import com.gistpetition.api.petition.domain.AgreementRepository;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.PetitionRepository;
import com.gistpetition.api.petition.dto.AgreementRequest;
import com.gistpetition.api.petition.dto.PetitionRequest;
import com.gistpetition.api.petition.dto.TempPetitionResponse;
import com.gistpetition.api.user.domain.UserRole;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static com.gistpetition.api.acceptance.common.TUser.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ReleasePetitionAcceptanceTest extends AcceptanceTest {

    @Autowired
    PetitionRepository petitionRepository;

    @Autowired
    AgreementRepository agreementRepository;

    @Test
    void test() {
        for (TUser tUser : values()) {
            if (!tUser.name().equals("T_ADMIN")) {
                tUser.doSignUp();
                System.out.println(tUser.getId());
            }
        }
    }

    @Test
    @DisplayName("청원을 만들고")
    void ReleasePetition() {
        EUNGI.doSignUp();

        PetitionRequest petitionRequest = new PetitionRequest("title", "description", Category.ACADEMIC.getId());
        Response createdPetition = EUNGI.doLoginAndThen().createPetition(petitionRequest);

        assertThat(createdPetition.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        String[] locationHeader = createdPetition.header(HttpHeaders.LOCATION).split("/");
        String tmpUrl = locationHeader[locationHeader.length - 1];

        for (TUser tUser : willAgreeUserArray()) {
            tUser.doSignUp();
            Response petition = tUser.doLoginAndThen().retrieveTempPetition(tmpUrl);
            TempPetitionResponse petitionResponse = petition.as(TempPetitionResponse.class);
            Long petitionId = petitionResponse.getId();

            AgreementRequest agreementRequest = new AgreementRequest("동의합니다. ");
            Response response = tUser.doLoginAndThen().agreePetition(agreementRequest, petitionId);
        }

        Response petition = T_ADMIN.doLoginAndThen().updateUserRoleAndThen(EUNGI, UserRole.MANAGER).retrieveTempPetition(tmpUrl);
        TempPetitionResponse response = petition.as(TempPetitionResponse.class);

        Response releasedPetition = T_ADMIN.doLoginAndThen().releasePetition(response.getId());

        EUNGI.doLoginAndThen().retrieveTempPetition(tmpUrl).as(TempPetitionResponse.class);


    }

    @AfterEach
    void tearDown() {
        TUser.clearAll();
        agreementRepository.deleteAllInBatch();
        petitionRepository.deleteAllInBatch();
    }


}

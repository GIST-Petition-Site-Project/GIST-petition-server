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

import java.util.List;

import static com.gistpetition.api.acceptance.common.TUser.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ReleasePetitionAcceptanceTest extends AcceptanceTest {

    @Autowired
    PetitionRepository petitionRepository;

    @Autowired
    AgreementRepository agreementRepository;

    @Test
    @DisplayName("청원을 만들고 5명이 동의해서 Manager가 release하는 테스트")
    void ReleasePetition() {
        EUNGI.doSignUp();

        PetitionRequest petitionRequest = new PetitionRequest("title", "description", Category.ACADEMIC.getId());
        Response createdPetition = EUNGI.doLoginAndThen().createPetition(petitionRequest);

        assertThat(createdPetition.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        String[] locationHeader = createdPetition.header(HttpHeaders.LOCATION).split("/");
        String tmpUrl = locationHeader[locationHeader.length - 1];

        agreePetitionByFiveUsers(tmpUrl);

        GUNE.doSignUp();
        Response petition = T_ADMIN.doLoginAndThen().updateUserRoleAndThen(GUNE, UserRole.MANAGER).retrieveTempPetition(tmpUrl);
        TempPetitionResponse willBeReleasedPetition = petition.as(TempPetitionResponse.class);

        GUNE.doLoginAndThen().releasePetition(willBeReleasedPetition.getId());

        Response releasedPetition = EUNGI.doLoginAndThen().retrieveReleasedPetition(willBeReleasedPetition.getId());
        assertThat(releasedPetition.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void agreePetitionByFiveUsers(String tmpUrl) {
        List<TUser> agreeUsers = List.of(AGREE_USER1, AGREE_USER2, AGREE_USER3, AGREE_USER4, AGREE_USER5);
        for (TUser tUser : agreeUsers) {
            tUser.doSignUp();
            Response petition = tUser.doLoginAndThen().retrieveTempPetition(tmpUrl);
            TempPetitionResponse petitionResponse = petition.as(TempPetitionResponse.class);
            Long petitionId = petitionResponse.getId();
            AgreementRequest agreementRequest = new AgreementRequest("동의합니다. ");
            tUser.doLoginAndThen().agreePetition(agreementRequest, petitionId);
        }
    }

    @AfterEach
    void tearDown() {
        TUser.clearAll();
        agreementRepository.deleteAllInBatch();
        petitionRepository.deleteAllInBatch();
    }


}

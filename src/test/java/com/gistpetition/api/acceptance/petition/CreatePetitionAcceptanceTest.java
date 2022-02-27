package com.gistpetition.api.acceptance.petition;

import com.gistpetition.api.acceptance.AcceptanceTest;
import com.gistpetition.api.acceptance.common.TUser;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.PetitionRepository;
import com.gistpetition.api.petition.dto.PetitionRequest;
import com.gistpetition.api.user.domain.UserRole;
import io.restassured.response.Response;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static com.gistpetition.api.acceptance.common.TUser.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CreatePetitionAcceptanceTest extends AcceptanceTest {
    @Autowired
    PetitionRepository petitionRepository;

    @Test
    void createPetitionByNormal() {
        KOSE.doSignUp();

        PetitionRequest petitionRequest = new PetitionRequest("title", "description", Category.ACADEMIC.getId());
        Response createPetition = KOSE.doLoginAndThen().createPetition(petitionRequest);

        assertThat(createPetition.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createPetition.header(HttpHeaders.LOCATION)).contains("/petitions/");
    }

    @Test
    void createPetitionByManager() {
        WANNTE.doSignUp();
        T_ADMIN.doLoginAndThen().updateUserRoleAndThen(WANNTE, UserRole.MANAGER);

        PetitionRequest petitionRequest = new PetitionRequest("titleOver10Characters", "description", Category.ACADEMIC.getId());
        Response createPetition = WANNTE.doLoginAndThen().createPetition(petitionRequest);

        assertThat(createPetition.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createPetition.header(HttpHeaders.LOCATION)).contains("/petitions/");
    }

    @AfterEach
    void tearDown() {
        TUser.clearAll();
        petitionRepository.deleteAllInBatch();
    }
}

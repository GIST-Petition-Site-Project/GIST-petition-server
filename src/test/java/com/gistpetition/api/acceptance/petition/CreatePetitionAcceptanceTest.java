package com.gistpetition.api.acceptance.petition;

import com.gistpetition.api.acceptance.common.TUser;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.PetitionRepository;
import com.gistpetition.api.petition.dto.PetitionRequest;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.verification.domain.SignUpVerificationInfoRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static com.gistpetition.api.acceptance.common.FirstAdmin.FIRST_ADMIN;
import static com.gistpetition.api.acceptance.common.TUser.MANAGER;
import static com.gistpetition.api.acceptance.common.TUser.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreatePetitionAcceptanceTest {
    @Autowired
    PetitionRepository petitionRepository;

    @Autowired
    SignUpVerificationInfoRepository signUpVerificationInfoRepository;

    @Autowired
    UserRepository userRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createPetitionByNormal() {
        NORMAL.doSignUp();

        PetitionRequest petitionRequest = new PetitionRequest("title", "description", Category.ACADEMIC.getId());
        Response createPetition = NORMAL.doLoginAndThen().createPetition(petitionRequest);

        assertThat(createPetition.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createPetition.header(HttpHeaders.LOCATION)).contains("/petitions/");
    }

    @Test
    void createPetitionByManager() {
        MANAGER.doSignUp();
        FIRST_ADMIN.login().updateUserRole(MANAGER);

        PetitionRequest petitionRequest = new PetitionRequest("titleOver10Characters", "description", Category.ACADEMIC.getId());
        Response createPetition = MANAGER.doLoginAndThen().createPetition(petitionRequest);

        assertThat(createPetition.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createPetition.header(HttpHeaders.LOCATION)).contains("/petitions/");
    }

    @AfterEach
    void tearDown() {
        TUser.clearAll();
        FIRST_ADMIN.clear();
        petitionRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
}

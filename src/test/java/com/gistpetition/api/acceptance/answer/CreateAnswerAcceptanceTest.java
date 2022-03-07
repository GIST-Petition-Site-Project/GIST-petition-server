package com.gistpetition.api.acceptance.answer;

import com.gistpetition.api.acceptance.AcceptanceTest;
import com.gistpetition.api.acceptance.common.TUser;
import com.gistpetition.api.answer.domain.AnswerRepository;
import com.gistpetition.api.answer.dto.AnswerRequest;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.PetitionRepository;
import com.gistpetition.api.petition.dto.request.PetitionRequest;
import com.gistpetition.api.petition.dto.response.PetitionResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gistpetition.api.acceptance.common.TUser.GUNE;
import static com.gistpetition.api.acceptance.common.TUser.T_MANAGER;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateAnswerAcceptanceTest extends AcceptanceTest {

    @Autowired
    private PetitionRepository petitionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @Test
    @DisplayName("GUNE이라는 User가 생성한 청원을 Manager가 답변을 주는 동시성 테스트")
    public void createAnswerWithConcurrency() throws InterruptedException {
        PetitionRequest petitionRequest = new PetitionRequest("title", "description", Category.ACADEMIC.getId());
        AnswerRequest answerRequest = new AnswerRequest("contents");

        GUNE.doSignUp();

        Response createPetition = GUNE.doLoginAndThen().createPetition(petitionRequest);
        assertThat(createPetition.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        String[] locationHeader = createPetition.header(HttpHeaders.LOCATION).split("/");
        String tmpUrl = locationHeader[locationHeader.length - 1];

        Response retrieveTempPetition = T_MANAGER.doLoginAndThen().retrieveTempPetition(tmpUrl);
        Long petitionId = retrieveTempPetition.as(PetitionResponse.class).getId();

        int numberOfThreads = 10;
        AtomicInteger errorCount = new AtomicInteger(0);
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                Response createAnswerResponse = T_MANAGER.doAct().createAnswer(petitionId, answerRequest);
                if (createAnswerResponse.statusCode() != HttpStatus.CREATED.value()) {
                    errorCount.incrementAndGet();
                }
                latch.countDown();
            });
        }
        latch.await();
        assertThat(errorCount.get()).isEqualTo(numberOfThreads - 1);
        assertThat(answerRepository.findAllByPetitionId(petitionId)).hasSize(1);
    }

    @AfterEach
    void tearDown() {
        TUser.clearAll();
        answerRepository.deleteAllInBatch();
        petitionRepository.deleteAllInBatch();
    }
}

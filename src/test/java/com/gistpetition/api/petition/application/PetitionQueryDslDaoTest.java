package com.gistpetition.api.petition.application;

import com.gistpetition.api.IntegrationTest;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.dto.AgreementRequest;
import com.gistpetition.api.petition.dto.AnswerRequest;
import com.gistpetition.api.petition.dto.PetitionPreviewResponse;
import com.gistpetition.api.petition.dto.PetitionRequest;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.gistpetition.api.petition.application.PetitionQueryCondition.ANSWERED;
import static com.gistpetition.api.petition.application.PetitionQueryCondition.ONGOING;
import static com.gistpetition.api.petition.domain.Petition.REQUIRED_AGREEMENT_FOR_ANSWER;
import static com.gistpetition.api.petition.domain.Petition.REQUIRED_AGREEMENT_FOR_RELEASE;
import static com.gistpetition.api.petition.domain.QPetition.petition;
import static org.assertj.core.api.Assertions.assertThat;

class PetitionQueryDslDaoTest extends IntegrationTest {
    private static final AgreementRequest AGREEMENT_REQUEST = new AgreementRequest("동의합니다.");
    private static final AnswerRequest ANSWER_REQUEST = new AnswerRequest("답변을 달았다");
    private static final PetitionRequest DORM_PETITION_REQUEST = new PetitionRequest("title", "description", Category.DORMITORY.getId());
    public static final String EMAIL = "email@gist.ac.kr";
    public static final String PASSWORD = "password";

    @Autowired
    private PetitionQueryDslDao petitionQueryDslDao;
    @Autowired
    private PetitionCommandService petitionCommandService;
    @Autowired
    private UserRepository userRepository;

    private User petitionOwner;

    @BeforeEach
    void setUp() {
        petitionOwner = userRepository.save(new User(EMAIL, PASSWORD, UserRole.USER));
    }

    @Test
    void retrieveOngoingPetition() {
        int numOfPetition = 3;
        List<Long> createdPetitionIds = new ArrayList<>();
        for (int i = 0; i < numOfPetition; i++) {
            createdPetitionIds.add(petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId()));
        }
        List<User> users = saveUsersNumberOf(REQUIRED_AGREEMENT_FOR_RELEASE);
        createdPetitionIds.forEach(i -> {
            agreePetitionBy(i, users);
            petitionCommandService.releasePetition(i);
        });

        Page<PetitionPreviewResponse> petitions = petitionQueryDslDao.findAll(null, ONGOING.at(Instant.now()), PageRequest.of(0, 10));
        assertThat(petitions.getContent()).hasSize(numOfPetition);
    }

    @Test
    void retrieveOngoingPetitionWithAgreeCountSort() {
        int numOfPetition = 3;
        List<Long> createdPetitionIds = new ArrayList<>();
        for (int i = 0; i < numOfPetition; i++) {
            createdPetitionIds.add(petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId()));
        }
        List<User> users = saveUsersNumberOf(REQUIRED_AGREEMENT_FOR_RELEASE);
        createdPetitionIds.forEach(i -> {
            agreePetitionBy(i, users);
            petitionCommandService.releasePetition(i);
        });

        User user1 = userRepository.save(new User("new@gist.ac.kr", "password", UserRole.USER));
        User user2 = userRepository.save(new User("new2@gist.ac.kr", "password", UserRole.USER));

        agreePetitionBy(createdPetitionIds.get(0), List.of(user1));
        agreePetitionBy(createdPetitionIds.get(1), List.of(user1, user2));

        Page<PetitionPreviewResponse> petitions = petitionQueryDslDao.findAll(null, ONGOING.at(Instant.now()), PageRequest.of(0, 10, Sort.Direction.DESC, "agreeCount"));
        assertThat(petitions.getContent()).hasSize(numOfPetition);
        assertThat(petitions.getContent().stream().map(PetitionPreviewResponse::getId))
                .containsExactly(createdPetitionIds.get(1), createdPetitionIds.get(0), createdPetitionIds.get(2));
    }

    @Test
    void retrieveAnsweredPetition() {
        int numOfPetition = 3;
        List<Long> createdPetitionIds = new ArrayList<>();
        for (int i = 0; i < numOfPetition; i++) {
            createdPetitionIds.add(petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId()));
        }
        List<User> users = saveUsersNumberOf(REQUIRED_AGREEMENT_FOR_ANSWER);
        createdPetitionIds.forEach(i -> {
            agreePetitionBy(i, users);
            petitionCommandService.releasePetition(i);
            petitionCommandService.answerPetition(i, ANSWER_REQUEST);
        });

        Page<PetitionPreviewResponse> petitions = petitionQueryDslDao.findAll(null, ANSWERED.at(Instant.now()), PageRequest.of(0, 10));
        assertThat(petitions).hasSize(numOfPetition);
    }

    @Test
    void retrievePetitionOfMine() {
        int numOfPetition = 3;
        for (int i = 0; i < numOfPetition; i++) {
            petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        }

        Page<PetitionPreviewResponse> petitions = petitionQueryDslDao.findAll(null, petition.userId.eq(petitionOwner.getId()), PageRequest.of(0, 10));
        assertThat(petitions).hasSize(numOfPetition);
    }

    private List<User> saveUsersNumberOf(int numberOfUsers) {
        List<User> users = LongStream.range(0, numberOfUsers)
                .mapToObj(i -> new User(i + EMAIL, PASSWORD, UserRole.USER)).collect(Collectors.toList());
        return userRepository.saveAll(users);
    }

    private void agreePetitionBy(Long petitionId, List<User> users) {
        users.forEach(user -> petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, user.getId()));
    }
}
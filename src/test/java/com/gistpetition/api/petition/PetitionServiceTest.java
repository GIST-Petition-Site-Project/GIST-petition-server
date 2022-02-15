package com.gistpetition.api.petition;

import com.gistpetition.api.ServiceTest;
import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.petition.application.PetitionService;
import com.gistpetition.api.petition.domain.*;
import com.gistpetition.api.petition.dto.*;
import com.gistpetition.api.user.domain.SimpleUser;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.RevisionMetadata;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PetitionServiceTest extends ServiceTest {
    private static final PetitionRequest DORM_PETITION_REQUEST = new PetitionRequest("title", "description", Category.DORMITORY.getId());
    private static final AgreementRequest AGREEMENT_REQUEST = new AgreementRequest("동의합니다.");
    @Autowired
    private PetitionService petitionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PetitionRepository petitionRepository;
    @Autowired
    private AgreementRepository agreementRepository;
    @Autowired
    private HttpSession httpSession;

    private User petitionOwner;

    @BeforeEach
    void setUp() {
        petitionOwner = userRepository.save(new User("email@email.com", "password", UserRole.USER));
    }

    @Test
    void createPetition() {
        UUID petitionUUID = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findByUuid(petitionUUID).orElseThrow(IllegalArgumentException::new);

        assertThat(petition.getTitle()).isEqualTo(DORM_PETITION_REQUEST.getTitle());
        assertThat(petition.getDescription()).isEqualTo(DORM_PETITION_REQUEST.getDescription());
        assertThat(petition.getCategory().getId()).isEqualTo(DORM_PETITION_REQUEST.getCategoryId());
        assertThat(petition.getUserId()).isEqualTo(petitionOwner.getId());
        assertThat(petition.getCreatedAt()).isNotNull();
    }

    @Test
    void findPageOfPetitions() {
        petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        Page<PetitionPreviewResponse> petitionPreviewResponses = petitionService.retrievePetition(pageable);
        assertThat(petitionPreviewResponses).hasSize(3);
    }

    @Test
    void updatePetition() {
        UUID petitionUUID = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findByUuid(petitionUUID).orElseThrow(IllegalArgumentException::new);

        LocalDateTime initialTime = petition.getUpdatedAt();
        PetitionRequest updateRequest = new PetitionRequest("updateTitle", "updateDescription", Category.FACILITY.getId());
        petitionService.updatePetition(petition.getId(), updateRequest);

        Petition updatedPetition = petitionRepository.findById(petition.getId()).orElseThrow(IllegalArgumentException::new);

        assertThat(updatedPetition.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(updatedPetition.getDescription()).isEqualTo(updateRequest.getDescription());
        assertThat(updatedPetition.getCategory()).isEqualTo(Category.of(updateRequest.getCategoryId()));
        assertTrue(updatedPetition.getUpdatedAt().isAfter(initialTime));
    }

    @Test
    void updatePetitionByNonExistentPetitionId() {
        UUID petitionUUID = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findByUuid(petitionUUID).orElseThrow(IllegalArgumentException::new);

        LocalDateTime initialTime = petition.getUpdatedAt();

        PetitionRequest petitionUpdateRequest = new PetitionRequest("updateTitle", "updateDescription", Category.FACILITY.getId());

        assertThatThrownBy(() -> petitionService.updatePetition(Long.MAX_VALUE, petitionUpdateRequest)).isInstanceOf(NoSuchPetitionException.class);

        Petition updatedPetition = petitionRepository.findByUuid(petitionUUID).orElseThrow(IllegalArgumentException::new);
        assertTrue(updatedPetition.getUpdatedAt().isEqual(initialTime));
    }

    @Test
    void agree() {
        UUID petitionUUID = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition p = petitionRepository.findByUuid(petitionUUID).orElseThrow();
        Petition petition = petitionRepository.findPetitionByWithEagerMode(p.getId());
        Assertions.assertThat(petition.getAgreements()).hasSize(0);

        petitionService.agree(AGREEMENT_REQUEST, petition.getId(), petitionOwner.getId());
        petition = petitionRepository.findPetitionByWithEagerMode(petition.getId());
        assertThat(petition.getAgreements()).hasSize(1);
        assertThat(petition.getAgreements().get(0).getDescription()).isEqualTo(AGREEMENT_REQUEST.getDescription());
    }

    @Test
    void agreeNotExistingPetitionId() {
        Long petitionId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> petitionService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId())
        ).isInstanceOf(NoSuchPetitionException.class);
    }

    @Test
    void agreeTwiceByOneUser() {
        UUID petitionUUID = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findByUuid(petitionUUID).orElseThrow();
        petitionService.agree(AGREEMENT_REQUEST, petition.getId(), petitionOwner.getId());

        assertThatThrownBy(
                () -> petitionService.agree(AGREEMENT_REQUEST, petition.getId(), petitionOwner.getId())
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void applyAgreementWithConcurrency() throws InterruptedException {
        UUID petitionUUID = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findByUuid(petitionUUID).orElseThrow();
        int numberOfThreads = 10;

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            AgreementRequest agreementRequest = new AgreementRequest("description" + i);
            service.execute(() -> {
                try {
                    petitionService.agree(agreementRequest, petition.getId(), petitionOwner.getId());
                } catch (DataIntegrityViolationException e) {
                    System.out.println("---동의 중복---" + agreementRequest.getDescription());
                }
                latch.countDown();
            });
        }
        latch.await();
        assertThat(petitionService.retrieveNumberOfAgreements(petition.getId())).isEqualTo(1);
    }

    @Test
    void getPageOfAgreements() {
        UUID petitionUUID = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findByUuid(petitionUUID).orElseThrow();
        User user1 = userRepository.save(new User("user1@gm.gist.ac.kr", "password", UserRole.USER));
        User user2 = userRepository.save(new User("user2@gm.gist.ac.kr", "password", UserRole.USER));

        petitionService.agree(AGREEMENT_REQUEST, petition.getId(), petitionOwner.getId());
        petitionService.agree(AGREEMENT_REQUEST, petition.getId(), user1.getId());
        petitionService.agree(AGREEMENT_REQUEST, petition.getId(), user2.getId());

        Pageable pageable = PageRequest.of(0, 3, Sort.Direction.DESC, "createdAt");
        Page<AgreementResponse> allOfAgreements = petitionService.retrieveAgreements(petition.getId(), pageable);
        assertThat(allOfAgreements).hasSize(3);

        Pageable pageableSizeAsTwo = PageRequest.of(0, 2, Sort.Direction.DESC, "createdAt");
        Page<AgreementResponse> twoOfAgreements = petitionService.retrieveAgreements(petition.getId(), pageableSizeAsTwo);
        assertThat(twoOfAgreements).hasSize(2);
    }

    @Test
    void numberOfAgreements() {
        UUID petitionUUID = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findByUuid(petitionUUID).orElseThrow();
        User user = userRepository.save(new User("email@email.com", "password", UserRole.USER));
        User user3 = userRepository.save(new User("email3@email.com", "password", UserRole.USER));

        assertThat(petitionService.retrieveNumberOfAgreements(petition.getId())).isEqualTo(0);

        petitionService.agree(AGREEMENT_REQUEST, petition.getId(), petitionOwner.getId());
        petitionService.agree(AGREEMENT_REQUEST, petition.getId(), user.getId());
        petitionService.agree(AGREEMENT_REQUEST, petition.getId(), user3.getId());

        assertThat(petitionService.retrieveNumberOfAgreements(petition.getId())).isEqualTo(3);
    }

    @Test
    void retrieveAgreedPetitions() {
        UUID petitionUUID1 = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        UUID petitionUUID2 = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        UUID petitionUUID3 = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition1 = petitionRepository.findByUuid(petitionUUID1).orElseThrow();
        Petition petition2 = petitionRepository.findByUuid(petitionUUID2).orElseThrow();
        Petition petition3 = petitionRepository.findByUuid(petitionUUID3).orElseThrow();
        User user = userRepository.save(new User("email@email.com", "password", UserRole.USER));
        User user3 = userRepository.save(new User("email3@email.com", "password", UserRole.USER));

        assertThat(petitionService.retrieveNumberOfAgreements(petition1.getId())).isEqualTo(0);

        petitionService.agree(AGREEMENT_REQUEST, petition1.getId(), petitionOwner.getId());
        petitionService.agree(AGREEMENT_REQUEST, petition1.getId(), user.getId());
        petitionService.agree(AGREEMENT_REQUEST, petition1.getId(), user3.getId());

        petitionService.agree(AGREEMENT_REQUEST, petition2.getId(), petitionOwner.getId());
        petitionService.agree(AGREEMENT_REQUEST, petition2.getId(), user.getId());
        petitionService.agree(AGREEMENT_REQUEST, petition2.getId(), user3.getId());

        petitionService.agree(AGREEMENT_REQUEST, petition3.getId(), petitionOwner.getId());
        petitionService.agree(AGREEMENT_REQUEST, petition3.getId(), user.getId());
        petitionService.agree(AGREEMENT_REQUEST, petition3.getId(), user3.getId());

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PetitionPreviewResponse> petitionPreviewResponses = petitionService.retrievePetition(pageRequest);

        assertThat(petitionService.retrieveNumberOfAgreements(petition1.getId())).isEqualTo(3);
    }

    @Test
    void getStateOfAgreement() {
        UUID petitionUUID = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findByUuid(petitionUUID).orElseThrow();
        assertThat(petitionService.retrieveStateOfAgreement(petition.getId(), petitionOwner.getId())).isFalse();

        petitionService.agree(AGREEMENT_REQUEST, petition.getId(), petitionOwner.getId());

        assertThat(petitionService.retrieveStateOfAgreement(petition.getId(), petitionOwner.getId())).isTrue();
        Agreement agreement = agreementRepository.findByUserId(petitionOwner.getId()).orElseThrow(IllegalArgumentException::new);
        assertThat(agreement.getCreatedAt()).isNotNull();
    }

    @Test
    void deletePetition() {
        Petition petition = petitionRepository.save(new Petition("title", "description", Category.DORMITORY, petitionOwner.getId()));
        petitionService.agree(AGREEMENT_REQUEST, petition.getId(), petitionOwner.getId());
        petitionService.deletePetition(petition.getId());
        assertFalse(petitionRepository.existsById(petition.getId()));
        PageRequest pageRequest = PageRequest.of(0, 10);
        assertThat(agreementRepository.findAgreementsByPetitionId(pageRequest, petition.getId())).hasSize(0);
    }

    @Test
    void deletePetitionByNonExistentPetitionId() {
        assertThatThrownBy(
                () -> petitionService.deletePetition(Long.MAX_VALUE)
        ).isInstanceOf(NoSuchPetitionException.class);
    }

    @Test
    void retrieveAnsweredPetition() {
        Petition petition = new Petition("title", "desc", Category.DORMITORY, petitionOwner.getId());
        petition.setAnswered(true);
        petitionRepository.save(petition);

        Pageable pageable = PageRequest.of(0, 10);
        assertThat(petitionService.retrieveAnsweredPetition(pageable).getContent()).hasSize(1);
    }


    @DisplayName("Insert, Update 수행 후의 revisionResponse 검증")
    @Test
    void retrieveRevisionsOfPetition() {
        httpSession.setAttribute("user", new SimpleUser(petitionOwner));
        PetitionRequest petitionRequest = new PetitionRequest("title", "desc", Category.DORMITORY.getId());
        UUID petitionUUID = petitionService.createPetition(petitionRequest, petitionOwner.getId());
        Petition petition = petitionRepository.findByUuid(petitionUUID).orElseThrow();
        petitionService.updatePetition(petition.getId(), new PetitionRequest("updateTitle", "updateDesc", Category.FACILITY.getId()));
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PetitionRevisionResponse> revisionResponses = petitionService.retrieveRevisionsOfPetition(petition.getId(), pageRequest);
        assertThat(revisionResponses.getContent()).hasSize(2);
        assertThat(revisionResponses.getContent()).allMatch(content -> content.getWorkedBy().equals(petitionOwner.getId()));
        List<PetitionRevisionResponse> content = revisionResponses.getContent();
        List<RevisionMetadata.RevisionType> revisionTypes = content.stream().map(PetitionRevisionResponse::getRevisionType).collect(Collectors.toList());
        assertThat(revisionTypes).containsExactly(RevisionMetadata.RevisionType.INSERT, RevisionMetadata.RevisionType.UPDATE);
    }

    @Test
    void retrievePetitionsWaitingForCheck() {
        UUID petitionUUID = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findByUuid(petitionUUID).orElseThrow();
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < Petition.REQUIRED_AGREEMENT_NUM; i++) {
            users.add(userRepository.save(new User(String.format("user%s@gist.ac.kr", i), "pwd", UserRole.USER)));
        }
        for (int i = 0; i < Petition.REQUIRED_AGREEMENT_NUM; i++) {
            petitionService.agree(AGREEMENT_REQUEST, petition.getId(), users.get(i).getId());
        }
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PetitionPreviewResponse> petitionPreviewResponses = petitionService.retrievePetitionsWaitingForCheck(pageRequest);
        assertThat(petitionPreviewResponses.getContent()).hasSize(1);
    }


    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        agreementRepository.deleteAllInBatch();
        petitionRepository.deleteAllInBatch();
    }
}

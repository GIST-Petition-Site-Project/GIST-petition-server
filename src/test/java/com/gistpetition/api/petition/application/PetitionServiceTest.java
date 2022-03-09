package com.gistpetition.api.petition.application;

import com.gistpetition.api.IntegrationTest;
import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.petition.PetitionBuilder;
import com.gistpetition.api.petition.domain.*;
import com.gistpetition.api.petition.dto.*;
import com.gistpetition.api.user.domain.SimpleUser;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.RevisionMetadata;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.gistpetition.api.petition.domain.Petition.REQUIRED_AGREEMENT_FOR_RELEASE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PetitionServiceTest extends IntegrationTest {
    public static final Instant PETITION_CREATION_AT = Instant.now();
    public static final Instant PETITION_EXPIRED_AT = PETITION_CREATION_AT.plusSeconds(Petition.POSTING_PERIOD_BY_SECONDS);
    private static final PetitionRequest DORM_PETITION_REQUEST = new PetitionRequest("title", "description", Category.DORMITORY.getId());
    private static final AgreementRequest AGREEMENT_REQUEST = new AgreementRequest("동의합니다.");

    public static final String EMAIL = "email@gist.ac.kr";
    public static final String PASSWORD = "password";
    @Autowired
    private PetitionQueryService petitionQueryService;
    @Autowired
    private PetitionCommandService petitionCommandService;
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
        petitionOwner = userRepository.save(new User(EMAIL, PASSWORD, UserRole.USER));
    }

    @Test
    void createPetition() {
        Long petitionId = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);

        assertThat(petition.getTitle()).isEqualTo(DORM_PETITION_REQUEST.getTitle());
        assertThat(petition.getDescription()).isEqualTo(DORM_PETITION_REQUEST.getDescription());
        assertThat(petition.getCategory().getId()).isEqualTo(DORM_PETITION_REQUEST.getCategoryId());
        assertThat(petition.getUserId()).isEqualTo(petitionOwner.getId());
        assertThat(petition.getCreatedAt()).isNotNull();
    }

    @Test
    void findPageOfPetitions() {
        petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
        Page<PetitionPreviewResponse> petitionPreviewResponses = petitionQueryService.retrievePetition(pageable);
        assertThat(petitionPreviewResponses).hasSize(3);
    }

    @Test
    void updatePetition() {
        Long petitionId = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);

        Instant initialTime = petition.getUpdatedAt();
        PetitionRequest updateRequest = new PetitionRequest("updateTitle", "updateDescription", Category.FACILITY.getId());
        petitionCommandService.updatePetition(petition.getId(), updateRequest);

        Petition updatedPetition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);

        assertThat(updatedPetition.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(updatedPetition.getDescription()).isEqualTo(updateRequest.getDescription());
        assertThat(updatedPetition.getCategory()).isEqualTo(Category.of(updateRequest.getCategoryId()));
        assertTrue(updatedPetition.getUpdatedAt().isAfter(initialTime));
    }

    @Test
    void updatePetitionByNonExistentPetitionId() {
        Long petitionId = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);

        Instant initialTime = petition.getUpdatedAt();

        PetitionRequest petitionUpdateRequest = new PetitionRequest("updateTitle", "updateDescription", Category.FACILITY.getId());
        assertThatThrownBy(() -> petitionCommandService.updatePetition(Long.MAX_VALUE, petitionUpdateRequest)).isInstanceOf(NoSuchPetitionException.class);

        Petition updatedPetition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);
        assertThat(updatedPetition.getUpdatedAt()).isEqualTo(initialTime);
    }

    @Test
    void agree() {
        Long petitionId = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findById(petitionId).orElseThrow();
        assertThat(petition.getAgreeCount()).isEqualTo(0);

        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId());

        petition = petitionRepository.findById(petitionId).orElseThrow();
        assertThat(petition.getAgreeCount()).isEqualTo(1);
    }

    @Test
    void agreeNotExistingPetitionId() {
        Long petitionId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId())
        ).isInstanceOf(NoSuchPetitionException.class);
    }

    @Test
    void agreeTwiceByOneUser() {
        Long petitionId = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());

        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId());

        assertThatThrownBy(
                () -> petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId())
        ).isInstanceOf(DuplicatedAgreementException.class);
    }

    @Test
    public void applyAgreementWithConcurrency() throws InterruptedException {
        Long petitionId = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        int numberOfThreads = 10;

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger errorCount = new AtomicInteger(0);
        for (int i = 0; i < numberOfThreads; i++) {
            AgreementRequest agreementRequest = new AgreementRequest("description" + i);
            service.execute(() -> {
                try {
                    petitionCommandService.agree(agreementRequest, petitionId, petitionOwner.getId());
                } catch (DuplicatedAgreementException e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        assertThat(errorCount.get()).isEqualTo(numberOfThreads - 1);
        assertThat(petitionQueryService.retrieveNumberOfAgreements(petitionId)).isEqualTo(1);
    }

    @Test
    void getPageOfAgreements() {
        Long petitionId = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());

        User user1 = userRepository.save(new User("user1@gm.gist.ac.kr", "password", UserRole.USER));
        User user2 = userRepository.save(new User("user2@gm.gist.ac.kr", "password", UserRole.USER));

        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId());
        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, user1.getId());
        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, user2.getId());

        Pageable pageable = PageRequest.of(0, 3, Sort.Direction.DESC, "createdAt");
        Page<AgreementResponse> allOfAgreements = petitionQueryService.retrieveAgreements(petitionId, pageable);
        assertThat(allOfAgreements).hasSize(3);

        Pageable pageableSizeAsTwo = PageRequest.of(0, 2, Sort.Direction.DESC, "createdAt");
        Page<AgreementResponse> twoOfAgreements = petitionQueryService.retrieveAgreements(petitionId, pageableSizeAsTwo);
        assertThat(twoOfAgreements).hasSize(2);
    }

    @Test
    void numberOfAgreements() {
        Long petitionId = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());

        User user = userRepository.save(new User("email@email.com", "password", UserRole.USER));
        User user3 = userRepository.save(new User("email3@email.com", "password", UserRole.USER));

        assertThat(petitionQueryService.retrieveNumberOfAgreements(petitionId)).isEqualTo(0);

        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId());
        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, user.getId());
        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, user3.getId());

        assertThat(petitionQueryService.retrieveNumberOfAgreements(petitionId)).isEqualTo(3);
    }

    @Test
    void retrieveAgreedPetitions() {
        Long petitionId = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Long petitionId2 = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Long petitionId3 = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());

        User user = userRepository.save(new User("email@email.com", "password", UserRole.USER));
        User user3 = userRepository.save(new User("email3@email.com", "password", UserRole.USER));

        assertThat(petitionQueryService.retrieveNumberOfAgreements(petitionId)).isEqualTo(0);

        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId());
        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, user.getId());
        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, user3.getId());

        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId2, petitionOwner.getId());
        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId2, user.getId());
        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId2, user3.getId());

        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId3, petitionOwner.getId());
        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId3, user.getId());
        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId3, user3.getId());

        assertThat(petitionQueryService.retrieveNumberOfAgreements(petitionId)).isEqualTo(3);
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

        Page<PetitionPreviewResponse> ongoingPetitions = petitionQueryService.retrieveOngoingPetition(Optional.empty(), PageRequest.of(0, 10));
        assertThat(ongoingPetitions.getContent()).hasSize(numOfPetition);

        for (PetitionPreviewResponse op : ongoingPetitions) {
            assertFalse(op.getExpired());
        }
    }

    @Test
    void getStateOfAgreement() {
        Long petitionId = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        assertThat(petitionQueryService.retrieveStateOfAgreement(petitionId, petitionOwner.getId())).isFalse();

        petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId());

        assertThat(petitionQueryService.retrieveStateOfAgreement(petitionId, petitionOwner.getId())).isTrue();
        Agreement agreement = agreementRepository.findByUserId(petitionOwner.getId()).orElseThrow(IllegalArgumentException::new);
        assertThat(agreement.getCreatedAt()).isNotNull();
    }

    @Test
    void deletePetition() {
        Petition petition = petitionRepository.save(
                PetitionBuilder.aPetition()
                        .withExpiredAt(PETITION_EXPIRED_AT)
                        .withUserId(petitionOwner.getId())
                        .build());
        petitionCommandService.agree(AGREEMENT_REQUEST, petition.getId(), petitionOwner.getId());

        petitionCommandService.deletePetition(petition.getId());
        assertFalse(petitionRepository.existsById(petition.getId()));
        PageRequest pageRequest = PageRequest.of(0, 10);
        assertThat(agreementRepository.findAgreementsByPetitionId(pageRequest, petition.getId())).hasSize(0);
    }

    @Test
    void deletePetitionByNonExistentPetitionId() {
        assertThatThrownBy(
                () -> petitionCommandService.deletePetition(Long.MAX_VALUE)
        ).isInstanceOf(NoSuchPetitionException.class);
    }

    @Test
    void retrieveAnsweredPetition() {
        Petition petition = PetitionBuilder.aPetition()
                .withExpiredAt(PETITION_EXPIRED_AT)
                .withUserId(petitionOwner.getId())
                .build();
        petition.setAnswered(true);
        petitionRepository.save(petition);

        Pageable pageable = PageRequest.of(0, 10);
        assertThat(petitionQueryService.retrieveAnsweredPetition(Optional.empty(), pageable).getContent()).hasSize(1);
    }

    @Test
    void retrieveAnsweredPetitionCount() {
        Petition petition = PetitionBuilder.aPetition()
                .withExpiredAt(PETITION_EXPIRED_AT)
                .withUserId(petitionOwner.getId())
                .build();
        petition.setAnswered(true);
        petitionRepository.save(petition);
        assertThat(petitionQueryService.retrieveAnsweredPetitionCount(Optional.empty())).isEqualTo(1L);
    }

    @DisplayName("Insert, Update 수행 후의 revisionResponse 검증")
    @Test
    void retrieveRevisionsOfPetition() {
        httpSession.setAttribute("user", new SimpleUser(petitionOwner));
        PetitionRequest petitionRequest = new PetitionRequest("title", "desc", Category.DORMITORY.getId());
        Long petitionId = petitionCommandService.createPetition(petitionRequest, petitionOwner.getId());

        petitionCommandService.updatePetition(petitionId, new PetitionRequest("updateTitle", "updateDesc", Category.FACILITY.getId()));
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PetitionRevisionResponse> revisionResponses = petitionQueryService.retrieveRevisionsOfPetition(petitionId, pageRequest);
        assertThat(revisionResponses.getContent()).hasSize(2);
        assertThat(revisionResponses.getContent()).allMatch(content -> content.getWorkedBy().equals(petitionOwner.getId()));
        List<PetitionRevisionResponse> content = revisionResponses.getContent();
        List<RevisionMetadata.RevisionType> revisionTypes = content.stream().map(PetitionRevisionResponse::getRevisionType).collect(Collectors.toList());
        assertThat(revisionTypes).containsExactly(RevisionMetadata.RevisionType.INSERT, RevisionMetadata.RevisionType.UPDATE);
    }

    @Test
    void release() {
        Long petitionId = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        List<User> users = saveUsersNumberOf(REQUIRED_AGREEMENT_FOR_RELEASE);
        agreePetitionBy(petitionId, users);

        petitionCommandService.releasePetition(petitionId);

        Petition releasedPetition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);
        assertTrue(releasedPetition.isReleased());
    }

    @Test
    void releaseNotExistingPetition() {
        Long petitionId = Long.MAX_VALUE;

        assertThatThrownBy(
                () -> petitionCommandService.releasePetition(petitionId)
        ).isInstanceOf(NoSuchPetitionException.class);
    }

    @Test
    void cancelRelease() {
        Long petitionId = petitionCommandService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        List<User> users = saveUsersNumberOf(REQUIRED_AGREEMENT_FOR_RELEASE);
        agreePetitionBy(petitionId, users);
        petitionCommandService.releasePetition(petitionId);

        petitionCommandService.cancelReleasePetition(petitionId);

        Petition cancelReleasedPetition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);
        assertFalse(cancelReleasedPetition.isReleased());
    }

    @Test
    void cancelReleaseNotExistingPetition() {
        Long petitionId = Long.MAX_VALUE;

        assertThatThrownBy(
                () -> petitionCommandService.cancelReleasePetition(petitionId)
        ).isInstanceOf(NoSuchPetitionException.class);
    }

    private List<User> saveUsersNumberOf(int numberOfUsers) {
        return LongStream.range(0, numberOfUsers)
                .mapToObj(i -> userRepository.save(new User(i + EMAIL, PASSWORD, UserRole.USER))).collect(Collectors.toList());
    }

    private void agreePetitionBy(Long petitionId, List<User> users) {
        users.forEach(user -> petitionCommandService.agree(AGREEMENT_REQUEST, petitionId, user.getId()));
    }
}

package com.gistpetition.api.petition;

import com.gistpetition.api.ServiceTest;
import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.petition.application.PetitionService;
import com.gistpetition.api.petition.domain.*;
import com.gistpetition.api.petition.dto.AgreementRequest;
import com.gistpetition.api.petition.dto.AgreementResponse;
import com.gistpetition.api.petition.dto.PetitionPreviewResponse;
import com.gistpetition.api.petition.dto.PetitionRequest;
import com.gistpetition.api.petition.dto.PetitionRevisionResponse;
import com.gistpetition.api.user.domain.SimpleUser;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.RevisionMetadata;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
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
        Long petitionId = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);

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
        Long petitionId = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);

        LocalDateTime initialTime = petition.getUpdatedAt();
        PetitionRequest updateRequest = new PetitionRequest("updateTitle", "updateDescription", Category.FACILITY.getId());
        petitionService.updatePetition(petition.getId(), updateRequest);

        Petition updatedPetition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);

        assertThat(updatedPetition.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(updatedPetition.getDescription()).isEqualTo(updateRequest.getDescription());
        assertThat(updatedPetition.getCategory()).isEqualTo(Category.of(updateRequest.getCategoryId()));
        assertTrue(updatedPetition.getUpdatedAt().isAfter(initialTime));
    }

    @Test
    void updatePetitionByNonExistentPetitionId() {
        Long petitionId = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        Petition petition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);

        LocalDateTime initialTime = petition.getUpdatedAt();

        PetitionRequest petitionUpdateRequest = new PetitionRequest("updateTitle", "updateDescription", Category.FACILITY.getId());

        assertThatThrownBy(() -> petitionService.updatePetition(Long.MAX_VALUE, petitionUpdateRequest)).isInstanceOf(NoSuchPetitionException.class);

        Petition updatedPetition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);
        assertTrue(updatedPetition.getUpdatedAt().isEqual(initialTime));
    }

    @Test
    void agree() {
        Long petitionId = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());

        Petition petition = petitionRepository.findPetitionByWithEagerMode(petitionId);
        Assertions.assertThat(petition.getAgreements()).hasSize(0);

        petitionService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId());
        petition = petitionRepository.findPetitionByWithEagerMode(petitionId);
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
    void getPageOfAgreements() {
        Long petitionId = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());

        User user1 = userRepository.save(new User("user1@gm.gist.ac.kr", "password", UserRole.USER));
        User user2 = userRepository.save(new User("user2@gm.gist.ac.kr", "password", UserRole.USER));

        petitionService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId());
        petitionService.agree(AGREEMENT_REQUEST, petitionId, user1.getId());
        petitionService.agree(AGREEMENT_REQUEST, petitionId, user2.getId());

        Pageable pageable = PageRequest.of(0, 3, Sort.Direction.DESC, "createdAt");
        Page<AgreementResponse> allOfAgreements = petitionService.retrieveAgreements(petitionId, pageable);
        assertThat(allOfAgreements).hasSize(3);

        Pageable pageableSizeAsTwo = PageRequest.of(0, 2, Sort.Direction.DESC, "createdAt");
        Page<AgreementResponse> twoOfAgreements = petitionService.retrieveAgreements(petitionId, pageableSizeAsTwo);
        assertThat(twoOfAgreements).hasSize(2);
    }

    @Test
    void numberOfAgreements() {
        Long petitionId = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());

        User user = userRepository.save(new User("email@email.com", "password", UserRole.USER));
        User user3 = userRepository.save(new User("email3@email.com", "password", UserRole.USER));

        assertThat(petitionService.retrieveNumberOfAgreements(petitionId)).isEqualTo(0);

        petitionService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId());
        petitionService.agree(AGREEMENT_REQUEST, petitionId, user.getId());
        petitionService.agree(AGREEMENT_REQUEST, petitionId, user3.getId());

        assertThat(petitionService.retrieveNumberOfAgreements(petitionId)).isEqualTo(3);
    }

    @Test
    void getStateOfAgreement() {
        Long petitionId = petitionService.createPetition(DORM_PETITION_REQUEST, petitionOwner.getId());
        assertThat(petitionService.retrieveStateOfAgreement(petitionId, petitionOwner.getId())).isFalse();

        petitionService.agree(AGREEMENT_REQUEST, petitionId, petitionOwner.getId());

        assertThat(petitionService.retrieveStateOfAgreement(petitionId, petitionOwner.getId())).isTrue();
        Agreement agreement = agreementRepository.findByUserId(petitionOwner.getId()).orElseThrow(IllegalArgumentException::new);
        assertThat(agreement.getCreatedAt()).isNotNull();
    }

    @Test
    void deletePetition() {
        Petition petition = petitionRepository.save(new Petition("title", "description", Category.DORMITORY, petitionOwner.getId()));
        petitionService.deletePetition(petition.getId());
        assertFalse(petitionRepository.existsById(petition.getId()));
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

    @Test
    void retrieveRevisionsOfPetition() {
        httpSession.setAttribute("user", new SimpleUser(petitionOwner));
        PetitionRequest petitionRequest = new PetitionRequest("title", "desc", Category.DORMITORY.getId());
        Long petitionId = petitionService.createPetition(petitionRequest, petitionOwner.getId());

        petitionService.updatePetition(petitionId, new PetitionRequest("updateTitle", "updateDesc", Category.FACILITY.getId()));
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PetitionRevisionResponse> revisionResponses = petitionService.retrieveRevisionsOfPetition(petitionId, pageRequest);
        assertThat(revisionResponses.getContent()).hasSize(2);
        assertThat(revisionResponses.getContent()).allMatch(content -> content.getWorkedBy() == petitionOwner.getId());
        List<PetitionRevisionResponse> content = revisionResponses.getContent();
        List<RevisionMetadata.RevisionType> revisionTypes = content.stream().map(PetitionRevisionResponse::getRevisionType).collect(Collectors.toList());
        assertThat(revisionTypes).containsExactly(RevisionMetadata.RevisionType.INSERT, RevisionMetadata.RevisionType.UPDATE);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        agreementRepository.deleteAllInBatch();
        petitionRepository.deleteAllInBatch();
    }
}

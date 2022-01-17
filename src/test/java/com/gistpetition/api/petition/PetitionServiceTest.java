package com.gistpetition.api.petition;

import com.gistpetition.api.ServiceTest;
import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.petition.application.PetitionService;
import com.gistpetition.api.petition.domain.*;
import com.gistpetition.api.petition.dto.PetitionRequest;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PetitionServiceTest extends ServiceTest {
    private static final PetitionRequest PETITION_REQUEST_DTO = new PetitionRequest("title", "description", 1L);
    @Autowired
    private PetitionService petitionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PetitionRepository petitionRepository;
    @Autowired
    private AgreementRepository agreementRepository;

    private User petitionOwner;

    @BeforeEach
    void setUp() {
        petitionOwner = userRepository.save(new User("email@email.com", "password", UserRole.USER));
    }

    @Test
    void createPetition() {
        Long petitionId = petitionService.createPetition(PETITION_REQUEST_DTO, petitionOwner.getId());
        Petition petition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);

        assertThat(petition.getTitle()).isEqualTo(PETITION_REQUEST_DTO.getTitle());
        assertThat(petition.getDescription()).isEqualTo(PETITION_REQUEST_DTO.getDescription());
        assertThat(petition.getCategory().getId()).isEqualTo(PETITION_REQUEST_DTO.getCategoryId());
        assertThat(petition.getUserId()).isEqualTo(petitionOwner.getId());
        Assertions.assertThat(petition.getCreatedAt()).isNotNull();
    }

    @Test
    void updatePetition() {
        Long petitionId = petitionService.createPetition(PETITION_REQUEST_DTO, petitionOwner.getId());
        Petition petition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);

        LocalDateTime initialTime = petition.getUpdatedAt();

        petitionService.updatePetitionDescription(petition.getId(), "updated");

        Petition updatedPetition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);
        LocalDateTime updatedTime = updatedPetition.getUpdatedAt();
        assertTrue(updatedTime.isAfter(initialTime));
    }

    @Test
    void updatePetitionByNonExistentPetitionId() {
        Long petitionId = petitionService.createPetition(PETITION_REQUEST_DTO, petitionOwner.getId());
        Petition petition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);

        LocalDateTime initialTime = petition.getUpdatedAt();

        assertThatThrownBy(() -> petitionService.updatePetitionDescription(Long.MAX_VALUE, "updated")).isInstanceOf(NoSuchPetitionException.class);

        Petition updatedPetition = petitionRepository.findById(petitionId).orElseThrow(IllegalArgumentException::new);
        LocalDateTime updatedTime = updatedPetition.getUpdatedAt();
        assertTrue(updatedTime.isEqual(initialTime));
    }

    @Test
    void agree() {
        Long petitionId = petitionService.createPetition(PETITION_REQUEST_DTO, petitionOwner.getId());

        Petition petition = petitionRepository.findPetitionByWithEagerMode(petitionId);
        Assertions.assertThat(petition.getAgreements()).hasSize(0);

        petitionService.agree(petitionId, petitionOwner.getId());
        petition = petitionRepository.findPetitionByWithEagerMode(petitionId);
        Assertions.assertThat(petition.getAgreements()).hasSize(1);
    }

    @Test
    void numberOfAgreements() {
        Long petitionId = petitionService.createPetition(PETITION_REQUEST_DTO, petitionOwner.getId());

        User user = userRepository.save(new User("email@email.com", "password", UserRole.USER));
        User user3 = userRepository.save(new User("email3@email.com", "password", UserRole.USER));

        assertThat(petitionService.getNumberOfAgreements(petitionId)).isEqualTo(0);

        petitionService.agree(petitionId, petitionOwner.getId());
        petitionService.agree(petitionId, user.getId());
        petitionService.agree(petitionId, user3.getId());

        assertThat(petitionService.getNumberOfAgreements(petitionId)).isEqualTo(3);
    }

    @Test
    void getStateOfAgreement() {
        Long petitionId = petitionService.createPetition(PETITION_REQUEST_DTO, petitionOwner.getId());
        assertThat(petitionService.getStateOfAgreement(petitionId, petitionOwner.getId())).isFalse();

        petitionService.agree(petitionId, petitionOwner.getId());

        assertThat(petitionService.getStateOfAgreement(petitionId, petitionOwner.getId())).isTrue();
        Agreement agreement = agreementRepository.findByUserId(petitionOwner.getId()).orElseThrow(IllegalArgumentException::new);
        Assertions.assertThat(agreement.getCreatedAt()).isNotNull();
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

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        agreementRepository.deleteAllInBatch();
        petitionRepository.deleteAllInBatch();
    }
}

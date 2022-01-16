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
    private static final PetitionRequest POST_REQUEST_DTO = new PetitionRequest("title", "description", 1L);
    @Autowired
    private PetitionService petitionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PetitionRepository petitionRepository;
    @Autowired
    private AgreementRepository agreementRepository;

    private User postOwner;

    @BeforeEach
    void setUp() {
        postOwner = userRepository.save(new User("email@email.com", "password", UserRole.USER));
    }

    @Test
    void createPost() {
        Long postId = petitionService.createPetition(POST_REQUEST_DTO, postOwner.getId());
        Petition petition = petitionRepository.findById(postId).orElseThrow(IllegalArgumentException::new);

        assertThat(petition.getTitle()).isEqualTo(POST_REQUEST_DTO.getTitle());
        assertThat(petition.getDescription()).isEqualTo(POST_REQUEST_DTO.getDescription());
        assertThat(petition.getCategory().getId()).isEqualTo(POST_REQUEST_DTO.getCategoryId());
        assertThat(petition.getUserId()).isEqualTo(postOwner.getId());
        Assertions.assertThat(petition.getCreatedAt()).isNotNull();
    }

    @Test
    void updatePost() {
        Long postId = petitionService.createPetition(POST_REQUEST_DTO, postOwner.getId());
        Petition petition = petitionRepository.findById(postId).orElseThrow(IllegalArgumentException::new);

        LocalDateTime initialTime = petition.getUpdatedAt();

        petitionService.updatePetitionDescription(petition.getId(), "updated");

        Petition updatedPetition = petitionRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        LocalDateTime updatedTime = updatedPetition.getUpdatedAt();
        assertTrue(updatedTime.isAfter(initialTime));
    }

    @Test
    void updatePostByNonExistentPostId() {
        Long postId = petitionService.createPetition(POST_REQUEST_DTO, postOwner.getId());
        Petition petition = petitionRepository.findById(postId).orElseThrow(IllegalArgumentException::new);

        LocalDateTime initialTime = petition.getUpdatedAt();

        assertThatThrownBy(() -> petitionService.updatePetitionDescription(Long.MAX_VALUE, "updated")).isInstanceOf(NoSuchPetitionException.class);

        Petition updatedPetition = petitionRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        LocalDateTime updatedTime = updatedPetition.getUpdatedAt();
        assertTrue(updatedTime.isEqual(initialTime));
    }

    @Test
    void agree() {
        Long postId = petitionService.createPetition(POST_REQUEST_DTO, postOwner.getId());

        Petition petition = petitionRepository.findPetitionByWithEagerMode(postId);
        Assertions.assertThat(petition.getAgreements()).hasSize(0);

        petitionService.agree(postId, postOwner.getId());
        petition = petitionRepository.findPetitionByWithEagerMode(postId);
        Assertions.assertThat(petition.getAgreements()).hasSize(1);
    }

    @Test
    void numberOfAgreements() {
        Long postId = petitionService.createPetition(POST_REQUEST_DTO, postOwner.getId());

        User user = userRepository.save(new User("email@email.com", "password", UserRole.USER));
        User user3 = userRepository.save(new User("email3@email.com", "password", UserRole.USER));

        assertThat(petitionService.getNumberOfAgreements(postId)).isEqualTo(0);

        petitionService.agree(postId, postOwner.getId());
        petitionService.agree(postId, user.getId());
        petitionService.agree(postId, user3.getId());

        assertThat(petitionService.getNumberOfAgreements(postId)).isEqualTo(3);
    }

    @Test
    void getStateOfAgreement() {
        Long postId = petitionService.createPetition(POST_REQUEST_DTO, postOwner.getId());
        assertThat(petitionService.getStateOfAgreement(postId, postOwner.getId())).isFalse();

        petitionService.agree(postId, postOwner.getId());

        assertThat(petitionService.getStateOfAgreement(postId, postOwner.getId())).isTrue();
        Agreement agreement = agreementRepository.findByUserId(postOwner.getId()).orElseThrow(IllegalArgumentException::new);
        Assertions.assertThat(agreement.getCreatedAt()).isNotNull();
    }

    @Test
    void deletePost() {
        Petition petition = petitionRepository.save(new Petition("title", "description", Category.DORMITORY, postOwner.getId()));
        petitionService.deletePetition(petition.getId());
        assertFalse(petitionRepository.existsById(petition.getId()));
    }

    @Test
    void deletePostByNonExistentPostId() {
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

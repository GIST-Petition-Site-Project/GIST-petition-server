package com.gistpetition.api.petition.application;


import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.domain.PetitionRepository;
import com.gistpetition.api.petition.dto.PetitionPreviewResponse;
import com.gistpetition.api.petition.dto.PetitionRequest;
import com.gistpetition.api.petition.dto.PetitionResponse;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PetitionService {

    private final PetitionRepository petitionRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long createPetition(PetitionRequest petitionRequest, Long userId) {
        return petitionRepository.save(
                new Petition(petitionRequest.getTitle(),
                        petitionRequest.getDescription(),
                        Category.getById(petitionRequest.getCategoryId()),
                        userId)
        ).getId();
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionByCategoryId(Long categoryId, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByCategory(Category.getById(categoryId), pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionByKeyword(String keyword, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByTitleContains(keyword, pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionsByUserId(Long user_id,Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByUserId(user_id,pageable));
    }

    @Transactional(readOnly = true)
    public PetitionResponse retrievePetitionById(Long petitionId) {
        return PetitionResponse.of(findPetitionById(petitionId));
    }

    @Transactional(readOnly = true)
    public Long getPetitionCount() {
        return petitionRepository.count();
    }

    @Transactional
    public void updatePetitionDescription(Long petitionId, String description) {
        Petition petition = findPetitionById(petitionId);
        petition.setDescription(description);
    }

    @Transactional
    public void deletePetition(Long petitionId) {
        if (!petitionRepository.existsById(petitionId)) {
            throw new NoSuchPetitionException();
        }
        petitionRepository.deleteById(petitionId);
        eventPublisher.publishEvent(new PetitionDeleteEvent(petitionId));
    }

    @Transactional
    public Boolean agree(Long petitionId, Long userId) {
        Petition petition = findPetitionById(petitionId);
        User user = findUserById(userId);
        return petition.applyAgreement(user);
    }

    @Transactional(readOnly = true)
    public int getNumberOfAgreements(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        return petition.getAgreements().size();
    }

    @Transactional(readOnly = true)
    public Boolean getStateOfAgreement(Long petitionId, Long userId) {
        Petition petition = findPetitionById(petitionId);
        User user = findUserById(userId);
        return petition.isAgreedBy(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(NoSuchUserException::new);
    }

    private Petition findPetitionById(Long petitionId) {
        return petitionRepository.findById(petitionId).orElseThrow(NoSuchPetitionException::new);
    }
}

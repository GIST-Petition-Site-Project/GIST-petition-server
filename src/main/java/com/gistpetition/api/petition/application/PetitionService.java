package com.gistpetition.api.petition.application;


import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.petition.domain.*;
import com.gistpetition.api.petition.dto.*;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.gistpetition.api.petition.domain.Petition.REQUIRED_AGREEMENT_NUM;

@Service
@AllArgsConstructor
public class PetitionService {

    private final PetitionRepository petitionRepository;
    private final AgreementRepository agreementRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long createPetition(PetitionRequest petitionRequest, Long userId) {
        return petitionRepository.save(
                new Petition(petitionRequest.getTitle(),
                        petitionRequest.getDescription(),
                        Category.of(petitionRequest.getCategoryId()),
                        userId)
        ).getId();
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionByCategoryId(Long categoryId, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByCategory(Category.of(categoryId), pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionByKeyword(String keyword, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByTitleContains(keyword, pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionsByUserId(Long userId, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByUserId(userId, pageable));
    }

    @Transactional(readOnly = true)
    public PetitionResponse retrievePetitionById(Long petitionId) {
        return PetitionResponse.of(findPetitionById(petitionId));
    }

    @Transactional(readOnly = true)
    public PetitionResponse retrievePetitionByUUID(String petitionUUID) {
        return PetitionResponse.of(findPetitionByUUID(petitionUUID));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveAnsweredPetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByAnsweredTrue(pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveTempPetitions(Pageable pageable) {
        Page<Petition> petitions = petitionRepository.findPetitionByAgreeCountIsGreaterThanEqualAndExposedFalse(REQUIRED_AGREEMENT_NUM, pageable);
        return PetitionPreviewResponse.pageOf(petitions);
    }

    @Transactional
    public void exposePetition(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        petition.setExposed(true);
    }

    @Transactional(readOnly = true)
    public Page<PetitionRevisionResponse> retrieveRevisionsOfPetition(Long petitionId, Pageable pageable) {
        return PetitionRevisionResponse.pageOf(petitionRepository.findRevisions(petitionId, pageable));
    }

    @Transactional(readOnly = true)
    public Long retrievePetitionCount() {
        return petitionRepository.count();
    }

    @Transactional
    public void updatePetition(Long petitionId, PetitionRequest petitionRequest) {
        Petition petition = findPetitionById(petitionId);
        petition.setTitle(petitionRequest.getTitle());
        petition.setCategory(Category.of(petitionRequest.getCategoryId()));
        petition.setDescription(petitionRequest.getDescription());
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
    public void agree(AgreementRequest request, Long petitionId, Long userId) {
        Petition petition = findPetitionById(petitionId);
        User user = findUserById(userId);
        Agreement agreement = new Agreement(request.getDescription(), user.getId());
        agreement.setPetition(petition);
        agreementRepository.save(agreement);
    }

    @Transactional(readOnly = true)
    public Page<AgreementResponse> retrieveAgreements(Long petitionId, Pageable pageable) {
        Page<Agreement> agreements = agreementRepository.findAgreementsByPetitionId(pageable, petitionId);
        return AgreementResponse.pageOf(agreements);
    }

    @Transactional(readOnly = true)
    public int retrieveNumberOfAgreements(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        return petition.getAgreements().size();
    }

    @Transactional(readOnly = true)
    public Boolean retrieveStateOfAgreement(Long petitionId, Long userId) {
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

    private Petition findPetitionByUUID(String petitionUUID) {
        return petitionRepository.findByUuid(UUID.fromString(petitionUUID)).orElseThrow(NoSuchPetitionException::new);
    }
}

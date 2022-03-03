package com.gistpetition.api.petition.application;

import com.gistpetition.api.exception.petition.NoSuchCategoryException;
import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.petition.NotReleasedPetitionException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.petition.domain.*;
import com.gistpetition.api.petition.dto.AgreementResponse;
import com.gistpetition.api.petition.dto.PetitionPreviewResponse;
import com.gistpetition.api.petition.dto.PetitionResponse;
import com.gistpetition.api.petition.dto.PetitionRevisionResponse;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.gistpetition.api.petition.domain.Petition.REQUIRED_AGREEMENT_FOR_ANSWER;
import static com.gistpetition.api.petition.domain.Petition.REQUIRED_AGREEMENT_FOR_RELEASE;

@Service
@RequiredArgsConstructor
public class PetitionQueryServiceImpl implements PetitionQueryService {

    private final PetitionRepository petitionRepository;
    private final AgreementRepository agreementRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAll(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveReleasedPetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAllByReleasedTrue(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveReleasedPetitionByCategoryId(Long categoryId, Pageable pageable) {
        Category category = getCategoryEnumById(categoryId);
        return PetitionPreviewResponse.pageOf(petitionRepository.findAllByCategoryAndReleasedTrue(category, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveReleasedAndExpiredPetitionByCategoryId(Long categoryId, Pageable pageable) {
        Category category = getCategoryEnumById(categoryId);
        return PetitionPreviewResponse.pageOf(petitionRepository.findReleasedAndExpiredPetition(category, Instant.now(), pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveOngoingPetitionByCategoryId(Long categoryId, Pageable pageable) {
        Category category = getCategoryEnumById(categoryId);
        return PetitionPreviewResponse.pageOf(petitionRepository.findReleasedAndUnAnsweredAndUnExpiredPetition(category, Instant.now(), pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionByKeyword(String keyword, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByTitleContains(keyword, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionsByUserId(Long userId, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByUserId(userId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForRelease(Pageable pageable) {
        Page<Petition> petitions = petitionRepository.findPetitionByAgreeCountIsGreaterThanEqualAndReleasedFalse(REQUIRED_AGREEMENT_FOR_RELEASE, pageable);
        return PetitionPreviewResponse.pageOf(petitions);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForAnswer(Pageable pageable) {
        Page<Petition> petitions = petitionRepository.findPetitionByAgreeCountIsGreaterThanEqualAndReleasedTrueAndAnsweredFalse(REQUIRED_AGREEMENT_FOR_ANSWER, pageable);
        return PetitionPreviewResponse.pageOf(petitions);
    }

    @Override
    @Transactional(readOnly = true)
    public Long retrieveWaitingForReleasePetitionCount() {
        return petitionRepository.countByAgreeCountIsGreaterThanEqualAndReleasedFalse(REQUIRED_AGREEMENT_FOR_RELEASE);
    }

    @Override
    @Transactional(readOnly = true)
    public Long retrieveWaitingForAnswerPetitionCount() {
        return petitionRepository.countByAgreeCountIsGreaterThanEqualAndReleasedTrueAndAnsweredFalse(REQUIRED_AGREEMENT_FOR_ANSWER);
    }

    @Override
    @Transactional(readOnly = true)
    public PetitionResponse retrieveReleasedPetitionById(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        if (!petition.isReleased()) {
            throw new NotReleasedPetitionException();
        }
        return PetitionResponse.of(petition);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveAnsweredPetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByAnsweredTrue(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionRevisionResponse> retrieveRevisionsOfPetition(Long petitionId, Pageable pageable) {
        return PetitionRevisionResponse.pageOf(petitionRepository.findRevisions(petitionId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Long retrieveReleasedPetitionCount() {
        return petitionRepository.countByReleasedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Long retrieveAnsweredPetitionCount() {
        return petitionRepository.countByAnsweredTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AgreementResponse> retrieveAgreements(Long petitionId, Pageable pageable) {
        Page<Agreement> agreements = agreementRepository.findAgreementsByPetitionId(pageable, petitionId);
        return AgreementResponse.pageOf(agreements);
    }

    @Override
    @Transactional(readOnly = true)
    public int retrieveNumberOfAgreements(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        return petition.getAgreeCount();
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean retrieveStateOfAgreement(Long petitionId, Long userId) {
        Petition petition = findPetitionById(petitionId);
        User user = findUserById(userId);
        return petition.isAgreedBy(user);
    }


    @Override
    @Transactional(readOnly = true)
    public PetitionResponse retrievePetitionByTempUrl(String tempUrl) {
        Petition petition = petitionRepository.findByTempUrl(tempUrl).orElseThrow(NoSuchPetitionException::new);
        return PetitionResponse.of(petition);
    }

    @Override
    @Transactional(readOnly = true)
    public String retrieveTempUrlOf(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        return petition.getTempUrl();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(NoSuchUserException::new);
    }

    private Petition findPetitionById(Long petitionId) {
        return petitionRepository.findById(petitionId).orElseThrow(NoSuchPetitionException::new);
    }

    private Category getCategoryEnumById(Long categoryId) {
        Category category;
        try {
            category = Category.of(categoryId);
        } catch (NoSuchCategoryException ex) {
            category = null;
        }
        return category;
    }
}

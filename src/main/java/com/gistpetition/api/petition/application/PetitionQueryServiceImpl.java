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

import static com.gistpetition.api.petition.application.PetitionQueryCondition.*;

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
        return PetitionPreviewResponse.pageOf(petitionRepository.findPageByCategory(EXPIRED, Instant.now(), pageable, category));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveOngoingPetitionByCategoryId(Long categoryId, Pageable pageable) {
        Category category = getCategoryEnumById(categoryId);
        return PetitionPreviewResponse.pageOf(petitionRepository.findPageByCategory(ONGOING, Instant.now(), pageable, category));
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
        Page<Petition> petitions = petitionRepository.findPage(WAITING_FOR_RELEASE, Instant.now(), pageable);
        return PetitionPreviewResponse.pageOf(petitions);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForAnswer(Pageable pageable) {
        Page<Petition> petitions = petitionRepository.findPage(WAITING_FOR_ANSWER, Instant.now(), pageable);
        return PetitionPreviewResponse.pageOf(petitions);
    }

    @Override
    @Transactional(readOnly = true)
    public Long retrieveWaitingForReleasePetitionCount() {
        return petitionRepository.count(WAITING_FOR_RELEASE, Instant.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Long retrieveWaitingForAnswerPetitionCount() {
        return petitionRepository.count(WAITING_FOR_ANSWER, Instant.now());
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

package com.gistpetition.api.petition.application;


import com.gistpetition.api.config.annotation.DataIntegrityHandler;
import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.petition.NotReleasedPetitionException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.petition.domain.*;
import com.gistpetition.api.petition.dto.*;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.utils.urlGenerator.UrlGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.gistpetition.api.petition.domain.Petition.*;

@Service
@RequiredArgsConstructor
public class PetitionService {
    public static final int TEMP_URL_LENGTH = 6;

    private final PetitionRepository petitionRepository;
    private final AgreementRepository agreementRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UrlGenerator urlGenerator;

    @Transactional
    public Long createPetition(PetitionRequest petitionRequest, Long userId) {
        String tempUrl = urlGenerator.generate(TEMP_URL_LENGTH);
        Petition created = petitionRepository.save(
                new Petition(
                        petitionRequest.getTitle(),
                        petitionRequest.getDescription(),
                        Category.of(petitionRequest.getCategoryId()),
                        Instant.now().plusSeconds(POSTING_PERIOD_BY_SECONDS),
                        userId,
                        tempUrl));
        return created.getId();
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveReleasedPetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAllByReleasedTrue(pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveReleasedPetitionByCategoryId(Long categoryId, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAllByCategoryAndReleasedTrue(Category.of(categoryId), pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveReleasedAndExpiredPetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAllByExpiredAtBeforeAndReleasedTrue(Instant.now(), pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveReleasedAndExpiredPetitionByCategoryId(Long categoryId, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAllByCategoryAndExpiredAtBeforeAndReleasedTrue(Category.of(categoryId), Instant.now(), pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveOngoingPetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAllByExpiredAtAfterAndReleasedTrueAndAnsweredFalse(Instant.now(), pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveOngoingPetitionByCategoryId(Long categoryId, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAllByCategoryAndExpiredAtAfterAndReleasedTrueAndAnsweredFalse(Category.of(categoryId), Instant.now(), pageable));
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
    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForRelease(Pageable pageable) {
        Page<Petition> petitions = petitionRepository.findPetitionByAgreeCountIsGreaterThanEqualAndReleasedFalse(REQUIRED_AGREEMENT_FOR_RELEASE, pageable);
        return PetitionPreviewResponse.pageOf(petitions);
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForAnswer(Pageable pageable) {
        Page<Petition> petitions = petitionRepository.findPetitionByAgreeCountIsGreaterThanEqualAndReleasedTrueAndAnsweredFalse(REQUIRED_AGREEMENT_FOR_ANSWER, pageable);
        return PetitionPreviewResponse.pageOf(petitions);
    }

    @Transactional(readOnly = true)
    public Long retrieveWaitingForReleasePetitionCount() {
        return petitionRepository.countByAgreeCountIsGreaterThanEqualAndReleasedFalse(REQUIRED_AGREEMENT_FOR_RELEASE);
    }

    @Transactional(readOnly = true)
    public Long retrieveWaitingForAnswerPetitionCount() {
        return petitionRepository.countByAgreeCountIsGreaterThanEqualAndReleasedTrueAndAnsweredFalse(REQUIRED_AGREEMENT_FOR_ANSWER);
    }

    @Transactional(readOnly = true)
    public PetitionResponse retrieveReleasedPetitionById(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        if (!petition.isReleased()) {
            throw new NotReleasedPetitionException();
        }
        return PetitionResponse.of(petition);
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveAnsweredPetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByAnsweredTrue(pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionRevisionResponse> retrieveRevisionsOfPetition(Long petitionId, Pageable pageable) {
        return PetitionRevisionResponse.pageOf(petitionRepository.findRevisions(petitionId, pageable));
    }

    @Transactional(readOnly = true)
    public Long retrieveReleasedPetitionCount() {
        return petitionRepository.countByReleasedTrue();
    }

    @Transactional(readOnly = true)
    public Long retrieveAnsweredPetitionCount() {
        return petitionRepository.countByAnsweredTrue();
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
    @DataIntegrityHandler(DuplicatedAgreementException.class)
    public void agree(AgreementRequest request, Long petitionId, Long userId) {
        Petition petition = findPetitionById(petitionId);
        User user = findUserById(userId);
        Agreement agreement = new Agreement(request.getDescription(), user.getId());
        agreement.setPetition(petition, Instant.now());
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
        return petition.getAgreeCount();
    }

    @Transactional(readOnly = true)
    public Boolean retrieveStateOfAgreement(Long petitionId, Long userId) {
        Petition petition = findPetitionById(petitionId);
        User user = findUserById(userId);
        return petition.isAgreedBy(user);
    }

    @Transactional
    public void releasePetition(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        petition.release(Instant.now());
    }

    @Transactional(readOnly = true)
    public PetitionResponse retrievePetitionByTempUrl(String tempUrl) {
        Petition petition = petitionRepository.findByTempUrl(tempUrl).orElseThrow(NoSuchPetitionException::new);
        return PetitionResponse.of(petition);
    }

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
}

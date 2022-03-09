package com.gistpetition.api.petition.application;

import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.petition.NotReleasedPetitionException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.petition.domain.*;
import com.gistpetition.api.petition.dto.*;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static com.gistpetition.api.petition.application.PetitionQueryCondition.*;

@Service
@RequiredArgsConstructor
public class PetitionQueryService {

    private final PetitionRepository petitionRepository;
    private final Answer2Repository answer2Repository;
    private final AgreementRepository agreementRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveReleasedPetition(Optional<Category> category, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAll(RELEASED_NOT_EXPIRED.of(category, Instant.now()), pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveReleasedAndExpiredPetition(Optional<Category> category, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAll(RELEASED_EXPIRED.of(category, Instant.now()), pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveOngoingPetition(Optional<Category> category, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAll(ONGOING.of(category, Instant.now()), pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForRelease(Optional<Category> category, Pageable pageable) {
        Page<Petition> petitions = petitionRepository.findAll(WAITING_FOR_RELEASE.of(category, Instant.now()), pageable);
        return PetitionPreviewResponse.pageOf(petitions);
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForAnswer(Optional<Category> category, Pageable pageable) {
        Page<Petition> petitions = petitionRepository.findAll(WAITING_FOR_ANSWER.of(category, Instant.now()), pageable);
        return PetitionPreviewResponse.pageOf(petitions);
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveAnsweredPetition(Optional<Category> category, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAll(ANSWERED.of(category, Instant.now()), pageable));
    }

    @Transactional(readOnly = true)
    public Long retrieveReleasedPetitionCount(Optional<Category> category) {
        return petitionRepository.count(RELEASED.of(category, Instant.now()));
    }

    @Transactional(readOnly = true)
    public Long retrieveWaitingForReleasePetitionCount(Optional<Category> category) {
        return petitionRepository.count(WAITING_FOR_RELEASE.of(category, Instant.now()));
    }

    @Transactional(readOnly = true)
    public Long retrieveWaitingForAnswerPetitionCount(Optional<Category> category) {
        return petitionRepository.count(WAITING_FOR_ANSWER.of(category, Instant.now()));
    }

    @Transactional(readOnly = true)
    public Long retrieveAnsweredPetitionCount(Optional<Category> category) {
        return petitionRepository.count(ANSWERED.of(category, Instant.now()));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionsByUserId(Long userId, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByUserId(userId, pageable));
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
    public Page<PetitionPreviewResponse> retrievePetitionByKeyword(String keyword, Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findByTitleContains(keyword, pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionRevisionResponse> retrieveRevisionsOfPetition(Long petitionId, Pageable pageable) {
        return PetitionRevisionResponse.pageOf(petitionRepository.findRevisions(petitionId, pageable));
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

    @Transactional(readOnly = true)
    public Answer2 retrieveAnswerByPetitionId(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        return petition.getAnswer2();
    }

    @Transactional(readOnly = true)
    public Page<AnswerRevisionResponse> retrieveRevisionsOfAnswer(Long petitionId, Pageable pageable) {
        Petition petition = findPetitionById(petitionId);
        return AnswerRevisionResponse.pageOf2(answer2Repository.findRevisions(petition.getAnswer2().getId(), pageable));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(NoSuchUserException::new);
    }

    private Petition findPetitionById(Long petitionId) {
        return petitionRepository.findById(petitionId).orElseThrow(NoSuchPetitionException::new);
    }
}

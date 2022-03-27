package com.gistpetition.api.petition.application;

import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.petition.NotReleasedPetitionException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.petition.domain.Agreement;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.domain.repository.AgreementRepository;
import com.gistpetition.api.petition.domain.repository.AnswerRepository;
import com.gistpetition.api.petition.domain.repository.PetitionQueryDslRepository;
import com.gistpetition.api.petition.domain.repository.PetitionRepository;
import com.gistpetition.api.petition.dto.*;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.gistpetition.api.petition.application.PetitionQueryCondition.*;
import static com.gistpetition.api.petition.domain.QPetition.petition;

@Service
@RequiredArgsConstructor
public class PetitionQueryService {

    private final PetitionRepository petitionRepository;
    private final AnswerRepository answerRepository;
    private final AgreementRepository agreementRepository;
    private final UserRepository userRepository;
    private final PetitionQueryDslRepository petitionQueryDslRepository;

    public Page<PetitionPreviewResponse> retrieveReleasedPetition(Pageable pageable) {
        return petitionQueryDslRepository.findAll(null, RELEASED_NOT_EXPIRED.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrieveReleasedPetition(Category category, Pageable pageable) {
        return petitionQueryDslRepository.findAll(category, RELEASED_NOT_EXPIRED.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrieveReleasedAndExpiredPetition(Pageable pageable) {
        return petitionQueryDslRepository.findAll(null, RELEASED_EXPIRED.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrieveReleasedAndExpiredPetition(Category category, Pageable pageable) {
        return petitionQueryDslRepository.findAll(category, RELEASED_EXPIRED.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrieveOngoingPetition(Pageable pageable) {
        return petitionQueryDslRepository.findAll(null, ONGOING.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrieveOngoingPetition(Category category, Pageable pageable) {
        return petitionQueryDslRepository.findAll(category, ONGOING.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForRelease(Pageable pageable) {
        return petitionQueryDslRepository.findAll(null, WAITING_FOR_RELEASE.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForRelease(Category category, Pageable pageable) {
        return petitionQueryDslRepository.findAll(category, WAITING_FOR_RELEASE.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForAnswer(Pageable pageable) {
        return petitionQueryDslRepository.findAll(null, WAITING_FOR_ANSWER.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForAnswer(Category category, Pageable pageable) {
        return petitionQueryDslRepository.findAll(category, WAITING_FOR_ANSWER.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrieveRejectedPetition(Pageable pageable) {
        return petitionQueryDslRepository.findAll(null, REJECTED.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrieveRejectedPetition(Category category, Pageable pageable) {
        return petitionQueryDslRepository.findAll(category, REJECTED.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrieveAnsweredPetition(Pageable pageable) {
        return petitionQueryDslRepository.findAll(null, ANSWERED.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrieveAnsweredPetition(Category category, Pageable pageable) {
        return petitionQueryDslRepository.findAll(category, ANSWERED.at(Instant.now()), pageable);
    }

    public Page<PetitionPreviewResponse> retrievePetitionsByUserId(Long userId, Pageable pageable) {
        return petitionQueryDslRepository.findAll(null, petition.userId.eq(userId), pageable);
    }

    public Long retrieveReleasedPetitionCount() {
        return petitionQueryDslRepository.count(null, RELEASED.at(Instant.now()));
    }

    public Long retrieveReleasedPetitionCount(Category category) {
        return petitionQueryDslRepository.count(category, RELEASED.at(Instant.now()));
    }

    public Long retrieveWaitingForReleasePetitionCount() {
        return petitionQueryDslRepository.count(null, WAITING_FOR_RELEASE.at(Instant.now()));
    }

    public Long retrieveWaitingForReleasePetitionCount(Category category) {
        return petitionQueryDslRepository.count(category, WAITING_FOR_RELEASE.at(Instant.now()));
    }

    public Long retrieveWaitingForAnswerPetitionCount() {
        return petitionQueryDslRepository.count(null, WAITING_FOR_ANSWER.at(Instant.now()));
    }

    public Long retrieveWaitingForAnswerPetitionCount(Category category) {
        return petitionQueryDslRepository.count(category, WAITING_FOR_ANSWER.at(Instant.now()));
    }

    public Long retrieveAnsweredPetitionCount() {
        return petitionQueryDslRepository.count(null, ANSWERED.at(Instant.now()));
    }

    public Long retrieveAnsweredPetitionCount(Category category) {
        return petitionQueryDslRepository.count(category, ANSWERED.at(Instant.now()));
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
    public Page<PetitionRevisionResponse> retrieveRevisionsOfPetition(Long petitionId, Pageable pageable) {
        return PetitionRevisionResponse.pageOf(petitionRepository.findRevisions(petitionId, pageable));
    }

    @Transactional(readOnly = true)
    public Page<AgreementResponse> retrieveAgreements(Long petitionId, Pageable pageable) {
        Page<Agreement> agreements = agreementRepository.findAgreementsByPetitionId(petitionId, pageable);
        return AgreementResponse.pageOf(agreements);
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
    public Page<AnswerRevisionResponse> retrieveRevisionsOfAnswer(Long petitionId, Pageable pageable) {
        Petition petition = findPetitionById(petitionId);
        Long answerId = petition.getAnswer().getId();
        return AnswerRevisionResponse.pageOf(answerRepository.findRevisions(answerId, pageable));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(NoSuchUserException::new);
    }

    private Petition findPetitionById(Long petitionId) {
        return petitionRepository.findById(petitionId).orElseThrow(NoSuchPetitionException::new);
    }
}

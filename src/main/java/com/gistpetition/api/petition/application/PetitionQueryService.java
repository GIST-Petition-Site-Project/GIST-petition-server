package com.gistpetition.api.petition.application;

import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.petition.NotReleasedPetitionException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.petition.domain.Agreement;
import com.gistpetition.api.petition.domain.Answer;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.domain.repository.AgreementRepository;
import com.gistpetition.api.petition.domain.repository.AnswerRepository;
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

@Service
@RequiredArgsConstructor
public class PetitionQueryService {

    private final PetitionRepository petitionRepository;
    private final AnswerRepository answerRepository;
    private final AgreementRepository agreementRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetition(Pageable pageable) {
        return PetitionPreviewResponse.pageOf(petitionRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveReleasedPetition(Category category, Pageable pageable) {
        return petitionRepository.findAll(category, RELEASED_NOT_EXPIRED.at(Instant.now()), pageable);
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveReleasedAndExpiredPetition(Category category, Pageable pageable) {
        return petitionRepository.findAll(category, RELEASED_EXPIRED.at(Instant.now()), pageable);
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveOngoingPetition(Category category, Pageable pageable) {
        return petitionRepository.findAll(category, ONGOING.at(Instant.now()), pageable);
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForRelease(Category category, Pageable pageable) {
        return petitionRepository.findAll(category, WAITING_FOR_RELEASE.at(Instant.now()), pageable);
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrievePetitionsWaitingForAnswer(Category category, Pageable pageable) {
        return petitionRepository.findAll(category, WAITING_FOR_ANSWER.at(Instant.now()), pageable);
    }

    @Transactional(readOnly = true)
    public Page<PetitionPreviewResponse> retrieveAnsweredPetition(Category category, Pageable pageable) {
        return petitionRepository.findAll(category, ANSWERED.at(Instant.now()), pageable);
    }

    @Transactional(readOnly = true)
    public Long retrieveReleasedPetitionCount(Category category) {
        return petitionRepository.count(category, RELEASED.at(Instant.now()));
    }

    @Transactional(readOnly = true)
    public Long retrieveWaitingForReleasePetitionCount(Category category) {
        return petitionRepository.count(category, WAITING_FOR_RELEASE.at(Instant.now()));
    }

    @Transactional(readOnly = true)
    public Long retrieveWaitingForAnswerPetitionCount(Category category) {
        return petitionRepository.count(category, WAITING_FOR_ANSWER.at(Instant.now()));
    }

    @Transactional(readOnly = true)
    public Long retrieveAnsweredPetitionCount(Category category) {
        return petitionRepository.count(category, ANSWERED.at(Instant.now()));
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
    public Answer retrieveAnswerByPetitionId(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        return petition.getAnswer();
    }

    @Transactional(readOnly = true)
    public Page<AnswerRevisionResponse> retrieveRevisionsOfAnswer(Long petitionId, Pageable pageable) {
        Petition petition = findPetitionById(petitionId);
        return AnswerRevisionResponse.pageOf2(answerRepository.findRevisions(petition.getAnswer().getId(), pageable));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(NoSuchUserException::new);
    }

    private Petition findPetitionById(Long petitionId) {
        return petitionRepository.findById(petitionId).orElseThrow(NoSuchPetitionException::new);
    }
}

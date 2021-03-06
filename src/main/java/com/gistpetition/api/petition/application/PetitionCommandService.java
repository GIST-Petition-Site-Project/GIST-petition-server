package com.gistpetition.api.petition.application;

import com.gistpetition.api.config.annotation.DataIntegrityHandler;
import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.petition.domain.AgreeCount;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.domain.repository.AgreeCountRepository;
import com.gistpetition.api.petition.domain.repository.PetitionRepository;
import com.gistpetition.api.petition.dto.AgreementRequest;
import com.gistpetition.api.petition.dto.AnswerRequest;
import com.gistpetition.api.petition.dto.PetitionRequest;
import com.gistpetition.api.petition.dto.RejectionRequest;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.utils.urlGenerator.UrlGenerator;
import com.gistpetition.api.utils.urlmatcher.UrlMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PetitionCommandService {

    private static final int TEMP_URL_LENGTH = 6;
    private final PetitionRepository petitionRepository;
    private final AgreeCountRepository agreeCountRepository;
    private final UserRepository userRepository;
    private final UrlGenerator urlGenerator;
    private final UrlMatcher urlMatcher;

    @Transactional
    public Long createPetition(PetitionRequest petitionRequest, Long userId) {
        Petition petition = new Petition(
                petitionRequest.getTitle(),
                petitionRequest.getDescription(),
                Category.of(petitionRequest.getCategoryId()),
                userId);
        String tempUrl = urlGenerator.generate(TEMP_URL_LENGTH);

        petition.placeTemporary(tempUrl, Instant.now());

        Petition created = petitionRepository.save(petition);
        agreeCountRepository.save(new AgreeCount(created.getId()));
        return created.getId();
    }

    @Transactional
    public void updatePetition(Long petitionId, PetitionRequest petitionRequest) {
        Petition petition = findPetitionById(petitionId);
        petition.update(petitionRequest.getTitle(), petitionRequest.getDescription(), petitionRequest.getCategoryId());
    }

    @Transactional
    public void deletePetition(Long petitionId) {
        if (!petitionRepository.existsById(petitionId)) {
            throw new NoSuchPetitionException();
        }
        petitionRepository.deleteById(petitionId);
    }

    @Transactional
    @DataIntegrityHandler(DuplicatedAgreementException.class)
    public void agree(AgreementRequest request, Long petitionId, Long userId) {
        Petition petition = findPetitionById(petitionId);
        User user = findUserById(userId);
        petition.agree(user.getId(), request.getDescription(), Instant.now());

        agreeCountRepository.incrementCount(petitionId);
    }

    @Transactional
    public void cancelReleasePetition(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        petition.cancelRelease();
    }

    @Transactional
    public void releasePetition(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        petition.release(Instant.now());
    }

    @Transactional
    public void rejectPetition(Long petitionId, RejectionRequest rejectionRequest) {
        Petition petition = findPetitionById(petitionId);
        petition.reject(rejectionRequest.getDescription(), Instant.now());
    }

    @Transactional
    public void updateRejection(Long petitionId, RejectionRequest rejectionRequest) {
        Petition petition = findPetitionById(petitionId);
        petition.updateRejection(rejectionRequest.getDescription());
    }

    @Transactional
    public void cancelRejection(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        petition.cancelRejection();
    }

    @Transactional
    public void answerPetition(Long petitionId, AnswerRequest answerRequest) {
        Petition petition = findPetitionById(petitionId);
        petition.answer(answerRequest.getDescription(), answerRequest.getVideoUrl(), urlMatcher);
    }

    @Transactional
    public void updateAnswer(Long petitionId, AnswerRequest updateAnswerRequest) {
        Petition petition = findPetitionById(petitionId);
        petition.updateAnswer(updateAnswerRequest.getDescription(), updateAnswerRequest.getVideoUrl(), urlMatcher);
    }

    @Transactional
    public void deleteAnswer(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        petition.deleteAnswer();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(NoSuchUserException::new);
    }

    private Petition findPetitionById(Long petitionId) {
        return petitionRepository.findById(petitionId).orElseThrow(NoSuchPetitionException::new);
    }
}

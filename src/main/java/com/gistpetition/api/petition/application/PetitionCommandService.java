package com.gistpetition.api.petition.application;

import com.gistpetition.api.config.annotation.DataIntegrityHandler;
import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.petition.domain.*;
import com.gistpetition.api.petition.dto.request.AgreementRequest;
import com.gistpetition.api.petition.dto.request.AnswerRequest;
import com.gistpetition.api.petition.dto.request.PetitionRequest;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.utils.urlGenerator.UrlGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.gistpetition.api.petition.domain.Petition.POSTING_PERIOD_BY_SECONDS;

@Service
@RequiredArgsConstructor
public class PetitionCommandService {

    private static final int TEMP_URL_LENGTH = 6;
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
    public void answerPetition(Long petitionId, AnswerRequest answerRequest) {
        Petition petition = findPetitionById(petitionId);
        petition.answer(answerRequest.getContent());
    }

    @Transactional
    public void updateAnswer(Long petitionId, AnswerRequest updateRequest) {
        Petition petition = findPetitionById(petitionId);
        petition.updateAnswer(updateRequest.getContent());
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

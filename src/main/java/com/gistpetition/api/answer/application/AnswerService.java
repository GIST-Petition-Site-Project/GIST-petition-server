package com.gistpetition.api.answer.application;

import com.gistpetition.api.answer.domain.Answer;
import com.gistpetition.api.answer.domain.AnswerRepository;
import com.gistpetition.api.config.annotation.DataIntegrityHandler;
import com.gistpetition.api.exception.petition.AlreadyAnswerException;
import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.petition.NotAnsweredPetitionException;
import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.domain.PetitionRepository;
import com.gistpetition.api.petition.dto.AnswerRequest;
import com.gistpetition.api.petition.dto.AnswerRevisionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final PetitionRepository petitionRepository;

    public AnswerService(AnswerRepository answerRepository,
                         PetitionRepository petitionRepository) {
        this.answerRepository = answerRepository;
        this.petitionRepository = petitionRepository;
    }

    @Transactional
    @DataIntegrityHandler(AlreadyAnswerException.class)
    public Long createAnswer(Long petitionId, AnswerRequest answerRequest) {
        Petition petition = findPetitionById(petitionId);
        if (petition.isAnswered()) {
            throw new AlreadyAnswerException();
        }
        Answer answer = new Answer(answerRequest.getContent(), petitionId);
        petition.setAnswered(true);
        return answerRepository.save(answer).getId();
    }

    @Transactional(readOnly = true)
    public Answer retrieveAnswerByPetitionId(Long petitionId) {
        checkExistenceOfPetition(petitionId);
        return findAnswerByPetitionId(petitionId);
    }

    @Transactional(readOnly = true)
    public Page<AnswerRevisionResponse> retrieveRevisionsOfAnswer(Long answerId, Pageable pageable) {
        return AnswerRevisionResponse.pageOf(answerRepository.findRevisions(answerId, pageable));
    }

    @Transactional(readOnly = true)
    public Long getNumberOfAnswers() {
        return answerRepository.count();
    }

    @Transactional
    public void updateAnswer(Long petitionId, AnswerRequest changeRequest) {
        checkExistenceOfPetition(petitionId);
        Answer answer = findAnswerByPetitionId(petitionId);
        answer.updateContent(changeRequest.getContent());
    }

    @Transactional
    public void deleteAnswer(Long petitionId) {
        Petition petition = findPetitionById(petitionId);
        checkExistenceOfAnswerOf(petitionId);
        answerRepository.deleteByPetitionId(petitionId);
        petition.setAnswered(false);
    }

    private Petition findPetitionById(Long petitionId) {
        return petitionRepository.findById(petitionId).orElseThrow(NoSuchPetitionException::new);
    }

    private void checkExistenceOfPetition(Long petitionId) {
        if (!petitionRepository.existsById(petitionId)) {
            throw new NoSuchPetitionException();
        }
    }

    private void checkExistenceOfAnswerOf(Long petitionId) {
        if (!answerRepository.existsByPetitionId(petitionId)) {
            throw new NotAnsweredPetitionException();
        }
    }

    private Answer findAnswerByPetitionId(Long petitionId) {
        return answerRepository.findByPetitionId(petitionId).orElseThrow(NotAnsweredPetitionException::new);
    }
}

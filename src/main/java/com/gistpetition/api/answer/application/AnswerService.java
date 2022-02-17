package com.gistpetition.api.answer.application;

import com.gistpetition.api.answer.domain.Answer;
import com.gistpetition.api.answer.domain.AnswerRepository;
import com.gistpetition.api.answer.dto.AnswerRequest;
import com.gistpetition.api.answer.dto.AnswerRevisionResponse;
import com.gistpetition.api.exception.petition.DuplicatedAnswerException;
import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.petition.UnAnsweredPetitionException;
import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.domain.PetitionRepository;
import org.springframework.dao.DataIntegrityViolationException;
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
    public Long createAnswer(Long petitionId, AnswerRequest answerRequest) {
        Petition petition = findPetitionById(petitionId);
        if (petition.isAnswered()) {
            throw new DuplicatedAnswerException();
        }
        Answer answer = new Answer(answerRequest.getContent(), petitionId);
        petition.setAnswered(true);
        Answer saved;
        try {
            saved = answerRepository.save(answer);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicatedAnswerException();
        }
        return saved.getId();
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
            throw new UnAnsweredPetitionException();
        }
    }

    private Answer findAnswerByPetitionId(Long petitionId) {
        return answerRepository.findByPetitionId(petitionId).orElseThrow(UnAnsweredPetitionException::new);
    }
}

package com.gistpetition.api.answer.application;

import com.gistpetition.api.answer.domain.AnswerRepository;
import com.gistpetition.api.petition.application.PetitionDeleteEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PetitionDeleteEventListenerOfAnswer implements ApplicationListener<PetitionDeleteEvent> {
    private final AnswerRepository answerRepository;

    public PetitionDeleteEventListenerOfAnswer(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    public void onApplicationEvent(PetitionDeleteEvent event) {
        answerRepository.deleteByPetitionId(event.getPetitionId());
    }
}
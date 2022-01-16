package com.gistpetition.api.comment.application;

import com.gistpetition.api.comment.domain.CommentRepository;
import com.gistpetition.api.petition.application.PetitionDeleteEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PetitionDeleteEventListenerOfComment implements ApplicationListener<PetitionDeleteEvent> {
    private final CommentRepository commentRepository;

    public PetitionDeleteEventListenerOfComment(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void onApplicationEvent(PetitionDeleteEvent event) {
        commentRepository.deleteByPetitionId(event.getPetitionId());
    }
}
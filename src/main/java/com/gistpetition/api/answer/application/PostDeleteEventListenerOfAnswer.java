package com.gistpetition.api.answer.application;

import com.gistpetition.api.answer.domain.AnswerRepository;
import com.gistpetition.api.post.application.PostDeleteEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PostDeleteEventListenerOfAnswer implements ApplicationListener<PostDeleteEvent> {
    private final AnswerRepository answerRepository;

    public PostDeleteEventListenerOfAnswer(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    public void onApplicationEvent(PostDeleteEvent event) {
        answerRepository.deleteByPostId(event.getPostId());
    }
}
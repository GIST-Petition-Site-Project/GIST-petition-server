package com.example.gistcompetitioncnserver.answer.application;

import com.example.gistcompetitioncnserver.answer.domain.AnswerRepository;
import com.example.gistcompetitioncnserver.post.PostDeleteEvent;
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
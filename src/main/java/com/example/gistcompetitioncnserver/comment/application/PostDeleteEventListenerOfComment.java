package com.example.gistcompetitioncnserver.comment.application;

import com.example.gistcompetitioncnserver.comment.domain.CommentRepository;
import com.example.gistcompetitioncnserver.post.application.PostDeleteEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PostDeleteEventListenerOfComment implements ApplicationListener<PostDeleteEvent> {
    private final CommentRepository commentRepository;

    public PostDeleteEventListenerOfComment(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void onApplicationEvent(PostDeleteEvent event) {
        commentRepository.deleteByPostId(event.getPostId());
    }
}
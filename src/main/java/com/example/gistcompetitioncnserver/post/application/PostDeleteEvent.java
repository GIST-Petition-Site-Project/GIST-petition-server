package com.example.gistcompetitioncnserver.post.application;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PostDeleteEvent extends ApplicationEvent {
    private final Long postId;

    public PostDeleteEvent(Long postId) {
        super(postId);
        this.postId = postId;
    }
}

package com.example.gistcompetitioncnserver.user;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private final Long userId;
    private final String appUrl;

    public OnRegistrationCompleteEvent(Long userId, String appUrl) {
        super(userId);
        this.userId = userId;
        this.appUrl = appUrl;
    }
}

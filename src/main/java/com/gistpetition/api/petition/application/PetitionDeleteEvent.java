package com.gistpetition.api.petition.application;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PetitionDeleteEvent extends ApplicationEvent {
    private final Long petitionId;

    public PetitionDeleteEvent(Long petitionId) {
        super(petitionId);
        this.petitionId = petitionId;
    }
}

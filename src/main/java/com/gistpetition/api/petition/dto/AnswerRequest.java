package com.gistpetition.api.petition.dto;

import javax.validation.constraints.NotBlank;

public class AnswerRequest {

    @NotBlank
    private String description;

    protected AnswerRequest() {
    }

    public AnswerRequest(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

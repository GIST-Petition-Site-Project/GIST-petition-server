package com.gistpetition.api.petition.dto;

import javax.validation.constraints.NotBlank;

public class AnswerRequest {

    @NotBlank
    private String content;

    protected AnswerRequest() {
    }

    public AnswerRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}

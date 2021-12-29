package com.example.gistcompetitioncnserver.answer;

import javax.validation.constraints.NotBlank;

public class AnswerRequest {

    @NotBlank
    private String content;

    protected AnswerRequest() {
    }

    public String getContent() {
        return content;
    }

    public AnswerRequest(String content) {
        this.content = content;
    }
}

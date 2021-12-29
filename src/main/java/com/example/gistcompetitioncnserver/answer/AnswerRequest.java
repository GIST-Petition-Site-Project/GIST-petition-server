package com.example.gistcompetitioncnserver.answer;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnswerRequest {
    private String content;

    public AnswerRequest(String content) {
        this.content = content;
    }
}

package com.example.gistcompetitioncnserver.answer;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnswerRequestDto {
    private String content;

    public AnswerRequestDto(String content) {
        this.content = content;
    }
}

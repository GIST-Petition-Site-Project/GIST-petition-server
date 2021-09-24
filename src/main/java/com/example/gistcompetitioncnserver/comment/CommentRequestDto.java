package com.example.gistcompetitioncnserver.comment;


import lombok.*;
import org.springframework.context.annotation.Bean;

@Getter
@NoArgsConstructor
public class CommentRequestDto {
    private String content;

    public CommentRequestDto(String content) {
        this.content = content;
    }

}

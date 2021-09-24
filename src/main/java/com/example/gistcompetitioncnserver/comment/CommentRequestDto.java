package com.example.gistcompetitioncnserver.comment;


import lombok.*;
import org.springframework.context.annotation.Bean;

@Getter
@NoArgsConstructor
public class CommentRequestDto {
    private Long userId;
    private String content;

    public CommentRequestDto(Long userId, String content) {
        this.userId = userId;
        this.content = content;
    }

}

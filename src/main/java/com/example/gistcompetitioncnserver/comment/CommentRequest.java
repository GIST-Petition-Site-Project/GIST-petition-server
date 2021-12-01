package com.example.gistcompetitioncnserver.comment;


import lombok.*;
import org.springframework.context.annotation.Bean;

@Getter
@NoArgsConstructor
public class CommentRequest {
    private String content;

    public CommentRequest(String content) {
        this.content = content;
    }

}

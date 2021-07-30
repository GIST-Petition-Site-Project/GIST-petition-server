package com.example.gistcompetitioncnserver.comment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

@Builder
@AllArgsConstructor
@Data
public class CommentRequestDto {
    private final Long userId;
    private final String content;
}

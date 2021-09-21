package com.example.gistcompetitioncnserver.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class PostRequestDto {
    private final String title;
    private final String description;
    private final String category;
    private final Long userId;
}

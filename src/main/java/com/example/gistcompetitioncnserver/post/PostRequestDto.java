package com.example.gistcompetitioncnserver.post;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private String title;
    private String description;
    private String category;

    @Builder // for test code
    public PostRequestDto(String title, String description, String category, Long userId) {
        this.title = title;
        this.description = description;
        this.category = category;
    }


}

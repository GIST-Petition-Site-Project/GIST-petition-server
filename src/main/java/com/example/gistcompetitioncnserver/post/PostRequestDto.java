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
    private Long userId;

    @Builder // for test code
    public PostRequestDto(String title, String description, String category, Long userId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.userId = userId;
    }

    public Post toEntity(){
        return Post.builder()
                .title(title)
                .description(description)
                .category(category)
                .userId(userId)
                .created(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }


}

package com.example.gistcompetitioncnserver.post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequest {
    private String title;
    private String description;
    private String category;

    @Builder
    public PostRequest(String title, String description, String category) {
        this.title = title;
        this.description = description;
        this.category = category;
    }
}

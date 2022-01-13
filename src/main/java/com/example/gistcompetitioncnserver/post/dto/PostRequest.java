package com.example.gistcompetitioncnserver.post.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PostRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private String category;

    protected PostRequest() {
    }

    public PostRequest(String title, String description, String category) {
        this.title = title;
        this.description = description;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

}

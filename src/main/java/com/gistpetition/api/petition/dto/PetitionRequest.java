package com.gistpetition.api.petition.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PetitionRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private Long categoryId;

    protected PetitionRequest() {
    }

    public PetitionRequest(String title, String description, Long categoryId) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

}

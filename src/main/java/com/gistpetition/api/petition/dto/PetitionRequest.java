package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

import static com.gistpetition.api.petition.domain.Petition.POSTING_PERIOD_BY_SECONDS;

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

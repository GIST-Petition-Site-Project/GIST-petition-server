package com.gistpetition.api.petition.dto;

import javax.validation.constraints.NotBlank;

public class AnswerRequest {

    @NotBlank
    private String description;
    private String videoUrl;

    protected AnswerRequest() {
    }

    public AnswerRequest(String description) {
        this(description, null);
    }

    public AnswerRequest(String description, String videoUrl) {
        this.description = description;
        this.videoUrl = videoUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}

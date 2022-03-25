package com.gistpetition.api.petition.dto;

import javax.validation.constraints.NotBlank;

public class RejectionRequest {

    @NotBlank
    private String description;

    protected RejectionRequest() {
    }

    public RejectionRequest(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

package com.gistpetition.api.petition.dto.request;


import javax.validation.constraints.NotBlank;

public class AgreementRequest {
    @NotBlank
    private String description;

    protected AgreementRequest() {
    }

    public String getDescription() {
        return description;
    }

    public AgreementRequest(String description) {
        this.description = description;
    }

}

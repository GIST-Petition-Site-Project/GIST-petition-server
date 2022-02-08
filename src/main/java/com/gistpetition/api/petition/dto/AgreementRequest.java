package com.gistpetition.api.petition.dto;


import javax.validation.constraints.NotBlank;

public class AgreementRequest {
    @NotBlank
    private String content;

    protected AgreementRequest() {
    }

    public String getContent() {
        return content;
    }

    public AgreementRequest(String content) {
        this.content = content;
    }

}

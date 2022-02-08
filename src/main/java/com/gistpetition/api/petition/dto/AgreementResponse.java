package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Agreement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class AgreementResponse {

    String description;

    public AgreementResponse(String description) {
        this.description = description;
    }

    public static AgreementResponse of(Agreement agreement) {
        return new AgreementResponse(agreement.getDescription());
    }

    public static List<AgreementResponse> listOf(List<Agreement> agreements) {
        return agreements.stream()
                .map(AgreementResponse::of)
                .collect(Collectors.toList());
    }
}

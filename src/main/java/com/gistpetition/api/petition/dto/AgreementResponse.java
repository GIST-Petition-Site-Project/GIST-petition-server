package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Agreement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class AgreementResponse {

    String description;
    Long agreementId;

    public AgreementResponse(String description, Long agreementId){
        this.description = description;
        this.agreementId = agreementId;
    }

    public static AgreementResponse of(Agreement agreement) {
        return new AgreementResponse(agreement.getDescription(), agreement.getId());
    }

    public static Page<AgreementResponse> pageOf(Page<Agreement> agreements) {
        return agreements.map(AgreementResponse::of);
    }
}

package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Petition;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TempPetitionResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final String categoryName;
    private final Boolean answered;
    private final Long userId;
    private final Integer agreements;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String tempUrl;

    public static TempPetitionResponse of(Petition petition, String tempUrl) {
        return new TempPetitionResponse(
                petition.getId(),
                petition.getTitle(),
                petition.getDescription(),
                petition.getCategory().getName(),
                petition.isAnswered(),
                petition.getUserId(),
                petition.getAgreements().size(),
                petition.getCreatedAt(),
                petition.getUpdatedAt(),
                tempUrl
        );
    }
}
package com.gistpetition.api.petition.dto.response;

import com.gistpetition.api.petition.domain.Petition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PetitionResponse {
    private Long id;
    private String title;
    private String description;
    private String categoryName;
    private Integer agreements;
    private Long createdAt;
    private Long updatedAt;
    private String tempUrl;
    private Boolean released;
    private Boolean answered;
    private Boolean expired;

    public static PetitionResponse of(Petition petition) {
        return new PetitionResponse(
                petition.getId(),
                petition.getTitle(),
                petition.getDescription(),
                petition.getCategory().getName(),
                petition.getAgreeCount(),
                petition.getCreatedAt().toEpochMilli(),
                petition.getUpdatedAt().toEpochMilli(),
                petition.getTempUrl(),
                petition.isReleased(),
                petition.isAnswered(),
                petition.isExpiredAt(Instant.now())
        );
    }
}

package com.gistpetition.api.petition.dto;

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
    private Boolean answered;
    private Integer agreements;
    private Instant createdAt;
    private Instant updatedAt;
    private String tempUrl;
    private Boolean expired;

    public static PetitionResponse of(Petition petition) {
        return new PetitionResponse(
                petition.getId(),
                petition.getTitle(),
                petition.getDescription(),
                petition.getCategory().getName(),
                petition.isAnswered(),
                petition.getAgreeCount(),
                petition.getCreatedAt(),
                petition.getUpdatedAt(),
                petition.getTempUrl(),
                petition.isExpiredAt(Instant.now())
        );
    }
}

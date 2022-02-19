package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Petition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TempPetitionResponse {
    private Long id;
    private String title;
    private String description;
    private String categoryName;
    private Boolean answered;
    private Long userId;
    private Integer agreements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tempUrl;

    public static TempPetitionResponse of(Petition petition, String tempUrl) {
        return new TempPetitionResponse(
                petition.getId(),
                petition.getTitle(),
                petition.getDescription(),
                petition.getCategory().getName(),
                petition.isAnswered(),
                petition.getUserId(),
                petition.getAgreeCount(),
                petition.getCreatedAt(),
                petition.getUpdatedAt(),
                tempUrl
        );
    }
}

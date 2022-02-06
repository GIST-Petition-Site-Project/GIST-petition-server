package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Petition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PetitionResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final String categoryName;
    private final Boolean answered;
    private final Long userId;
    private final Integer agreements;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static Page<PetitionResponse> pageOf(Page<Petition> page) {
        return page.map(PetitionResponse::of);
    }

    public static PetitionResponse of(Petition petition) {
        return new PetitionResponse(
                petition.getId(),
                petition.getTitle(),
                petition.getDescription(),
                petition.getCategory().getName(),
                petition.isAnswered(),
                petition.getUserId(),
                petition.getAgreements().size(),
                petition.getCreatedAt(),
                petition.getUpdatedAt()
        );
    }
}

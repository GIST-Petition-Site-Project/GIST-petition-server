package com.gistpetition.api.petition.dto.response;

import com.gistpetition.api.petition.domain.Petition;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.time.Instant;

@Data
public class PetitionPreviewResponse {
    private final Long id;
    private final String title;
    private final String categoryName;
    private final Integer agreements;
    private final Long createdAt;
    private final String tempUrl;
    private final Boolean released;
    private final Boolean answered;
    private final Boolean expired;

    public static PetitionPreviewResponse of(Petition petition) {
        return new PetitionPreviewResponse(
                petition.getId(),
                petition.getTitle(),
                petition.getCategory().getName(),
                petition.getAgreeCount(),
                petition.getCreatedAt().toEpochMilli(),
                petition.getTempUrl(),
                petition.isReleased(),
                petition.isAnswered(),
                petition.isExpiredAt(Instant.now())
        );
    }

    public static Page<PetitionPreviewResponse> pageOf(Page<Petition> page) {
        return page.map(PetitionPreviewResponse::of);
    }
}

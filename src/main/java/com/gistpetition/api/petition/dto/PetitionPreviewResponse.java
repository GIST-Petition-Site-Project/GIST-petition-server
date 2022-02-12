package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Petition;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Data
public class PetitionPreviewResponse {
    private final Long id;
    private final String title;
    private final String categoryName;
    private final Integer agreements;
    private final LocalDateTime createdAt;

    public static Page<PetitionPreviewResponse> pageOf(Page<Petition> page) {
        return page.map(PetitionPreviewResponse::of);
    }

    public static PetitionPreviewResponse of(Petition petition) {
        return new PetitionPreviewResponse(
                petition.getId(),
                petition.getTitle(),
                petition.getCategory().getName(),
                petition.getAgreements().size(),
                petition.getCreatedAt()
        );
    }
}

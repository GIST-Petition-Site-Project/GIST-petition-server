package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class PetitionPreviewResponse {
    private Long id;
    private String title;
    private String categoryName;
    private Integer agreements;
    private Long createdAt;
    private String tempUrl;
    private Boolean released;
    private Boolean answered;
    private Boolean expired;

    @QueryProjection
    public PetitionPreviewResponse(Long id, String title, Category category, Instant createdAt, Instant expiredAt, Integer agreeCount, String tempUrl, Boolean released, Boolean answered) {
        this.id = id;
        this.title = title;
        this.categoryName = category.getName();
        this.createdAt = createdAt.toEpochMilli();
        this.agreements = agreeCount;
        this.tempUrl = tempUrl;
        this.released = released;
        this.answered = answered;
        this.expired = expiredAt.isBefore(Instant.now());
    }

    public static PetitionPreviewResponse of(Petition petition) {
        return new PetitionPreviewResponse(
                petition.getId(),
                petition.getTitle(),
                petition.getCategory(),
                petition.getCreatedAt(),
                petition.getExpiredAt(),
                petition.getAgreeCount(),
                petition.getTempUrl(),
                petition.isReleased(),
                petition.isAnswered()
        );
    }

    public static Page<PetitionPreviewResponse> pageOf(Page<Petition> page) {
        return page.map(PetitionPreviewResponse::of);
    }
}

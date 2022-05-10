package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Category;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class PetitionPreviewResponse {
    private Long id;
    private String title;
    private String categoryName;
    private Integer agreeCount;
    private Long createdAt;
    private Long waitingForAnswerAt;
    private String tempUrl;
    private Boolean released;
    private Boolean rejected;
    private Boolean answered;
    private Boolean expired;

    @QueryProjection
    public PetitionPreviewResponse(Long id, String title, Category category, Instant createdAt, Instant expiredAt, Instant waitingForAnswerAt, Integer agreeCount, String tempUrl, Boolean released, Boolean rejected, Boolean answered) {
        this.id = id;
        this.title = title;
        this.categoryName = category.getName();
        this.createdAt = createdAt.toEpochMilli();
        this.waitingForAnswerAt = waitingForAnswerAt != null ? waitingForAnswerAt.toEpochMilli() : null;
        this.agreeCount = agreeCount;
        this.tempUrl = tempUrl;
        this.released = released;
        this.rejected = rejected;
        this.answered = answered;
        this.expired = expiredAt.isBefore(Instant.now());
    }
}

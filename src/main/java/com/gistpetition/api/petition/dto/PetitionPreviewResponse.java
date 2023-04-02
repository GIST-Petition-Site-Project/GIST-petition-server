package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Status;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class PetitionPreviewResponse {
    private Long id;
    private String title;
    private Long categoryId;
    private String categoryName;
    private String status;
    private Integer agreeCount;
    private Long createdAt;
    private Long waitingForAnswerAt;
    private String tempUrl;
    private Boolean expired;

    @QueryProjection
    public PetitionPreviewResponse(Long id, String title, Category category, Status status, Instant createdAt, Instant expiredAt, Instant waitingForAnswerAt, Integer agreeCount, String tempUrl) {
        this.id = id;
        this.title = title;
        this.categoryId = category.getId();
        this.categoryName = category.getName();
        this.status = status.name();
        this.createdAt = createdAt.toEpochMilli();
        this.waitingForAnswerAt = waitingForAnswerAt != null ? waitingForAnswerAt.toEpochMilli() : null;
        this.agreeCount = agreeCount;
        this.tempUrl = tempUrl;
        this.expired = expiredAt.isBefore(Instant.now());
    }
}

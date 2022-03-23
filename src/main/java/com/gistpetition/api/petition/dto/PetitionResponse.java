package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Answer;
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
    private Integer agreeCount;
    private Long createdAt;
    private Long updatedAt;
    private String tempUrl;
    private Boolean released;
    private Boolean answered;
    private Boolean expired;
    private AnswerResponse answer;

    public static PetitionResponse of(Petition petition) {
        Answer answer = petition.getAnswer();
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
                petition.isExpiredAt(Instant.now()),
                answer != null ? AnswerResponse.of(answer) : null
        );
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerResponse {
        private Long id;
        private String description;
        private String videoUrl;
        private Long createdAt;
        private Long updatedAt;

        public static AnswerResponse of(Answer answer) {
            return new AnswerResponse(
                    answer.getId(),
                    answer.getDescription(),
                    answer.getVideoUrl(),
                    answer.getCreatedAt().toEpochMilli(),
                    answer.getUpdatedAt().toEpochMilli()
            );
        }
    }
}

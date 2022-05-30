package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Answer;
import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.domain.Rejection;
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
    private Long categoryId;
    private String status;
    private Integer agreeCount;
    private Long createdAt;
    private Long updatedAt;
    private Long waitingForAnswerAt;
    private String tempUrl;
    private Boolean released;
    private Boolean rejected;
    private Boolean answered;
    private Boolean expired;
    private RejectionResponse rejection;
    private AnswerResponse answer;

    public static PetitionResponse of(Petition petition) {
        Instant waitingForAnswerAt = petition.getWaitingForAnswerAt();
        Rejection rejection = petition.getRejection();
        Answer answer = petition.getAnswer();
        return new PetitionResponse(
                petition.getId(),
                petition.getTitle(),
                petition.getDescription(),
                petition.getCategory().getId(),
                petition.getStatus().name(),
                petition.getAgreeCount(),
                petition.getCreatedAt().toEpochMilli(),
                petition.getUpdatedAt().toEpochMilli(),
                waitingForAnswerAt != null ? waitingForAnswerAt.toEpochMilli() : null,
                petition.getTempUrl(),
                !petition.isTemporary(),
                petition.isRejected(),
                petition.isAnswered(),
                petition.isExpiredAt(Instant.now()),
                rejection != null ? RejectionResponse.of(rejection) : null,
                answer != null ? AnswerResponse.of(answer) : null
        );
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RejectionResponse {
        private Long id;
        private String description;
        private Long createdAt;
        private Long updatedAt;

        public static RejectionResponse of(Rejection rejection) {
            return new RejectionResponse(
                    rejection.getId(),
                    rejection.getDescription(),
                    rejection.getCreatedAt().toEpochMilli(),
                    rejection.getUpdatedAt().toEpochMilli()
            );
        }
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

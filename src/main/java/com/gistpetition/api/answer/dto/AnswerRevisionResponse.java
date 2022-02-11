package com.gistpetition.api.answer.dto;

import com.gistpetition.api.answer.domain.Answer;
import com.gistpetition.api.common.persistence.CustomRevisionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AnswerRevisionResponse {
    private Long revisionId;
    private Instant revisionTime;
    private RevisionMetadata.RevisionType revisionType;
    private Long workedBy;
    private String answerContent;

    public static AnswerRevisionResponse of(Revision<Long, Answer> revision) {
        CustomRevisionEntity customRevisionEntity = revision.getMetadata().getDelegate();
        return new AnswerRevisionResponse(
                revision.getRequiredRevisionNumber(),
                revision.getRequiredRevisionInstant(),
                revision.getMetadata().getRevisionType(),
                customRevisionEntity.getUserId(),
                revision.getEntity().getContent()
        );
    }

    public static Page<AnswerRevisionResponse> pageOf(Page<Revision<Long, Answer>> revisions) {
        return revisions.map(AnswerRevisionResponse::of);
    }
}

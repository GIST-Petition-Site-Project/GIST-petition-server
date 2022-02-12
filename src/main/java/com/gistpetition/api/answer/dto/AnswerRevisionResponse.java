package com.gistpetition.api.answer.dto;

import com.gistpetition.api.answer.domain.Answer;
import com.gistpetition.api.common.persistence.CustomRevisionEntity;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;

import java.time.Instant;

@Data
public class AnswerRevisionResponse {
    private final Long revisionId;
    private final Instant revisionTime;
    private final RevisionMetadata.RevisionType revisionType;
    private final Long workedBy;
    private final String answerContent;

    public static AnswerRevisionResponse of(Revision<Long, Answer> revision) {
        return new AnswerRevisionResponse(
                revision.getRequiredRevisionNumber(),
                revision.getRequiredRevisionInstant(),
                revision.getMetadata().getRevisionType(),
                revision.getMetadata().<CustomRevisionEntity>getDelegate().getUserId(),
                revision.getEntity().getContent()
        );
    }

    public static Page<AnswerRevisionResponse> pageOf(Page<Revision<Long, Answer>> revisions) {
        return revisions.map(AnswerRevisionResponse::of);
    }
}

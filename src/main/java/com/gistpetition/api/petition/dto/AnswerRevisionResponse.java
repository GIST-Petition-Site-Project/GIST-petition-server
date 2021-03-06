package com.gistpetition.api.petition.dto;

import com.gistpetition.api.common.persistence.CustomRevisionEntity;
import com.gistpetition.api.petition.domain.Answer;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;

@Data
public class AnswerRevisionResponse {
    private final Long revisionId;
    private final Long revisionTime;
    private final RevisionMetadata.RevisionType revisionType;
    private final Long workedBy;
    private final String answerDescription;
    private final String answerYoutubeUrl;

    public static AnswerRevisionResponse of(Revision<Long, Answer> revision) {
        return new AnswerRevisionResponse(
                revision.getRequiredRevisionNumber(),
                revision.getRequiredRevisionInstant().toEpochMilli(),
                revision.getMetadata().getRevisionType(),
                revision.getMetadata().<CustomRevisionEntity>getDelegate().getUserId(),
                revision.getEntity().getDescription(),
                revision.getEntity().getVideoUrl()
        );
    }

    public static Page<AnswerRevisionResponse> pageOf(Page<Revision<Long, Answer>> revisions) {
        return revisions.map(AnswerRevisionResponse::of);
    }
}

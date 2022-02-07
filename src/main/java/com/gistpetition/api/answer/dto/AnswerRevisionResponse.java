package com.gistpetition.api.answer.dto;

import com.gistpetition.api.answer.domain.Answer;
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
    private Integer revisionId;
    private Instant revisionTime;
    private RevisionMetadata.RevisionType revisionType;
    private Long userId;
    private String answerContent;


    public static AnswerRevisionResponse of(Revision<Integer, Answer> revision) {
        return new AnswerRevisionResponse(
                revision.getRequiredRevisionNumber(),
                revision.getRequiredRevisionInstant(),
                revision.getMetadata().getRevisionType(),
                revision.getEntity().getUserId(),
                revision.getEntity().getContent()
        );
    }

    public static Page<AnswerRevisionResponse> pageOf(Page<Revision<Integer, Answer>> revisions) {
        return revisions.map(AnswerRevisionResponse::of);
    }
}

package com.gistpetition.api.petition.dto;

import com.gistpetition.api.common.persistence.CustomRevisionEntity;
import com.gistpetition.api.petition.domain.Petition;
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
public class PetitionRevisionResponse {
    private Long revisionId;
    private Instant revisionTime;
    private RevisionMetadata.RevisionType revisionType;
    private Long workedBy;
    private String petitionDescription;

    public static PetitionRevisionResponse of(Revision<Long, Petition> revision) {
        CustomRevisionEntity customRevisionEntity = revision.getMetadata().getDelegate();
        return new PetitionRevisionResponse(
                revision.getRequiredRevisionNumber(),
                revision.getRequiredRevisionInstant(),
                revision.getMetadata().getRevisionType(),
                customRevisionEntity.getUserId(),
                revision.getEntity().getDescription()
        );
    }

    public static Page<PetitionRevisionResponse> pageOf(Page<Revision<Long, Petition>> revisions) {
        return revisions.map(PetitionRevisionResponse::of);
    }
}

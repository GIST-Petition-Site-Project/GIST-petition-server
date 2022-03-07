package com.gistpetition.api.petition.dto.response;

import com.gistpetition.api.common.persistence.CustomRevisionEntity;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PetitionRevisionResponse {
    private Long revisionId;
    private Long revisionTime;
    private RevisionMetadata.RevisionType revisionType;
    private Long workedBy;
    private String petitionTitle;
    private String petitionDescription;
    private Category petitionCategory;

    public static PetitionRevisionResponse of(Revision<Long, Petition> revision) {
        CustomRevisionEntity customRevisionEntity = revision.getMetadata().getDelegate();
        return new PetitionRevisionResponse(
                revision.getRequiredRevisionNumber(),
                revision.getRequiredRevisionInstant().toEpochMilli(),
                revision.getMetadata().getRevisionType(),
                customRevisionEntity.getUserId(),
                revision.getEntity().getTitle(),
                revision.getEntity().getDescription(),
                revision.getEntity().getCategory()
        );
    }

    public static Page<PetitionRevisionResponse> pageOf(Page<Revision<Long, Petition>> revisions) {
        return revisions.map(PetitionRevisionResponse::of);
    }
}

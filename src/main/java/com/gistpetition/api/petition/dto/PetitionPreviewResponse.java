package com.gistpetition.api.petition.dto;

import com.gistpetition.api.petition.domain.Petition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PetitionPreviewResponse {
    private final Long id;
    private final String title;
    private final String categoryName;
    private final Integer agreements;
    private final LocalDateTime createdAt;

    public static Page<PetitionPreviewResponse> pageOf(Page<Petition> page) {
        List<PetitionPreviewResponse> petitionResponseList = page.getContent().stream().map(PetitionPreviewResponse::of).collect(Collectors.toList());
        return new PageImpl<>(petitionResponseList, page.getPageable(), page.getTotalElements());
    }

    public static PetitionPreviewResponse of(Petition petition) {
        return new PetitionPreviewResponse(
                petition.getId(),
                petition.getTitle(),
                petition.getCategory().getName(),
                petition.getAgreements().size(),
                petition.getCreatedAt()
        );
    }
}

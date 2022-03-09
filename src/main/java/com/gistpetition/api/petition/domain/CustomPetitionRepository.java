package com.gistpetition.api.petition.domain;

import com.gistpetition.api.petition.dto.PetitionPreviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface CustomPetitionRepository {
    Page<PetitionPreviewResponse> findOngoingPetition(Instant at, Category category, Pageable pageable);

    Page<PetitionPreviewResponse> findAnsweredPetition(Category category, Pageable pageable);
}


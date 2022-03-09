package com.gistpetition.api.petition.domain;

import com.gistpetition.api.petition.dto.PetitionPreviewResponse;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPetitionRepository {

    Page<PetitionPreviewResponse> findAll(Category category, Predicate predicate, Pageable pageable);

    Long count(Category category, Predicate predicate);
}


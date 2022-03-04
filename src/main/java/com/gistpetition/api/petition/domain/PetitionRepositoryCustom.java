package com.gistpetition.api.petition.domain;

import com.gistpetition.api.petition.application.PetitionQueryCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface PetitionRepositoryCustom {

    Page<Petition> findAll(PetitionQueryCondition condition, Category category, Instant at, Pageable pageable);

    Long count(PetitionQueryCondition condition, Category category, Instant at);
}

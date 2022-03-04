package com.gistpetition.api.petition.domain;

import com.gistpetition.api.petition.application.PetitionQueryCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;

public interface PetitionRepositoryCustom {

    Page<Petition> findAll(PetitionQueryCondition condition, Optional<Category> category, Instant at, Pageable pageable);

    Long count(PetitionQueryCondition condition, Optional<Category> category, Instant at);
}

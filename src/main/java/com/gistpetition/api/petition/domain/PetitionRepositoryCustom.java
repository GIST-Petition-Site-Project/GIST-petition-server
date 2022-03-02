package com.gistpetition.api.petition.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface PetitionRepositoryCustom {

    Page<Petition> findPetitionByAgreeCountIsGreaterThanEqualAndReleasedFalse(int requiredAgreeCount, Pageable pageable);

    Page<Petition> findReleasedAndExpiredPetition(Category category, Instant at, Pageable pageable);

    Page<Petition> findReleasedAndUnAnsweredAndUnExpiredPetition(Category category, Instant at, Pageable pageable);
}

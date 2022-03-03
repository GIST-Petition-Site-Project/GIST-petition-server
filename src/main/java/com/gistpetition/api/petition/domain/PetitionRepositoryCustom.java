package com.gistpetition.api.petition.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface PetitionRepositoryCustom {

    // 유일한 un-released
    // waiting for released
    Page<Petition> findPetitionByAgreeCountIsGreaterThanEqualAndReleasedFalse(int requiredAgreeCount, Pageable pageable);

    // 게시될 expired
    Page<Petition> findReleasedAndExpiredPetition(Category category, Instant at, Pageable pageable);

    // 유일한 un-answered
    // waiting for answered
    Page<Petition> findReleasedAndUnAnsweredAndUnExpiredPetition(Category category, Instant at, Pageable pageable);
}

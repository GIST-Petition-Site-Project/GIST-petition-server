package com.gistpetition.api.petition.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TempPetitionUrlRepository extends JpaRepository<TempPetitionUrl, Long> {
    Boolean existsByTempUrlEquals(String tempUrl);

    Boolean existsByPetitionId(Long petitionId);

    Optional<TempPetitionUrl> findByTempUrl(String tempUrl);
}

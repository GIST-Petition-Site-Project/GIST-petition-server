package com.gistpetition.api.petition.domain.repository;

import com.gistpetition.api.petition.domain.AgreeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgreeCountRepository extends JpaRepository<AgreeCount, Long> {
    Optional<AgreeCount> findByPetitionId(Long petitionId);
}

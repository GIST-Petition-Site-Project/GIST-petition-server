package com.gistpetition.api.petition.domain.repository;

import com.gistpetition.api.petition.domain.AgreeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface AgreeCountRepository extends JpaRepository<AgreeCount, Long> {

    Optional<AgreeCount> findByPetitionId(Long petitionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AgreeCount a where a.petitionId = ?1")
    Optional<AgreeCount> findByPetitionIdWithLock(Long petitionId);
}
